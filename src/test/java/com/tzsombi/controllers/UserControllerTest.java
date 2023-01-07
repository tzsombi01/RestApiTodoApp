package com.tzsombi.controllers;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.ErrorConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private final UserRepository userRepository;

    @Autowired
    UserControllerTest(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegister_ValidUser() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Zsombor\", " +
                          "\"email\":\"someemail@gmail.com\", " +
                          "\"password\":\"password\", " +
                          "\"admin\":true}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotRegister_InValidUserEmail_AlreadyExists() throws Exception {
        // given
        String email = "someemail@gmail.com";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setPassword("password");
        user.setAdmin(true);

        // when
        given(userRepository.existsByEmail(email)).willReturn(true);

        // then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Zsombor\", " +
                                "\"email\":\"someemail@gmail.com\", " +
                                "\"password\":\"password\", " +
                                "\"admin\":true}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE));
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldDelete_UserById() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        // when
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // then
        mockMvc.perform(delete("/api/users/delete/{deleterUserId}?userIdToDelete=1", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn_AllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdate_User() throws Exception {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        String modifyingEmail = "anotheremail@gmail.com";
        String modifyingName = "AnotherName";
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // then
        mockMvc.perform(put("/api/users/update/{modifierUserId}" +
                        "?userIdToModify=1&name=AnotherName&email=anotheremail@gmail.com", 1))
                .andExpect(status().isOk());

        assertThat(user.getName()).isEqualTo(modifyingName);
        assertThat(user.getEmail()).isEqualTo(modifyingEmail);
    }

    @Test
    void shouldOnlyUpdate_UserName_EmailAlreadyInUse() throws Exception {
        // given
        String originalEmail = "someemail@gmail.com";
        String inUseEmail = "anotheremail@gmail.com";
        String modifyingName = "AnotherName";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(inUseEmail)).willReturn(true);

        // then
        mockMvc.perform(put("/api/users/update/{modifierUserId}" +
                        "?userIdToModify=1&name=AnotherName&email=anotheremail@gmail.com", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE));

        assertThat(user.getName()).isEqualTo(modifyingName);
        assertThat(user.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void shouldNotUpdate_User_EmailAlreadyInUse_And_NameIsNull() throws Exception {
        // given
        String originalEmail = "someemail@gmail.com";
        String modifyingEmail = "anotheremail@gmail.com";
        String password = "password";
        String originalName = "Zsombor";
        User user = new User();
        user.setId(1L);
        user.setName(originalName);
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(modifyingEmail)).willReturn(true);

        // then
        mockMvc.perform(put("/api/users/update/{modifierUserId}" +
                        "?userIdToModify=1&email=anotheremail@gmail.com", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE));

        assertThat(user.getName()).isEqualTo(originalName);
        assertThat(user.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void shouldNotUpdate_User_EmailIsNull_And_NameIsZeroCharLong() throws Exception {
        // given
        String originalEmail = "someemail@gmail.com";
        String modifyingEmail = "anotheremail@gmail.com";
        String password = "password";
        String originalName = "Zsombor";
        User user = new User();
        user.setId(1L);
        user.setName(originalName);
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // then
        mockMvc.perform(put("/api/users/update/{modifierUserId}" +
                        "?userIdToModify=1&name=", 1))
                .andExpect(status().isOk());

        assertThat(user.getName()).isEqualTo(originalName);
        assertThat(user.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void shouldLogin_ValidUser() throws Exception {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //then
        mockMvc.perform(post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{" +
                            "\"email\":\"someemail@gmail.com\"," +
                            "\"password\": \"password\"" +
                            "}"))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldNotLogin_inValidUserEmail() throws Exception {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"someemail123@gmail.com\"," +
                                "\"password\": \"password\"" +
                                "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD));
    }

    @Test
    void shouldNotLogin_inValidUserPassword() throws Exception {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        //then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"someemail2@gmail.com\"," +
                                "\"password\": \"123\"" +
                                "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD));
    }
}
