package com.tzsombi.controllers;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.services.EmailSendingObserver;
import com.tzsombi.services.UserService;
import com.tzsombi.utils.ErrorConstants;
import com.tzsombi.utils.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    private final MockMvc mockMvc;

    private final UserRepository userRepository;

    private UserService userService;

    @Mock private Logger logger;

    @Mock private EmailSendingObserver emailSendingObserver;

    @Autowired
    UserControllerTest(MockMvc mockMvc, UserRepository userRepository, UserService userService) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, logger, emailSendingObserver);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegister_ValidUser() throws Exception {
        // when
        // then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Zsombor\", " +
                          "\"email\":\"someemail@gmail.com\", " +
                          "\"password\":\"password\", " +
                          "\"admin\":true" +
                        "}"))
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

        User existingUser = new User();
        existingUser.setName("Zsombor");
        existingUser.setEmail(email);
        existingUser.setPassword("password");
        existingUser.setAdmin(true);

        // when
        userRepository.save(existingUser);
        // then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Zsombor\", " +
                                "\"email\":\"someemail@gmail.com\", " +
                                "\"password\":\"password\", " +
                                "\"admin\":true" +
                                "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE));
    }

    @Test
    void shouldDelete_UserById() throws Exception {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        userRepository.save(user);
        // when
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
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(password);

        // when
        User createdUser = userRepository.save(user);

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
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword("password");

        User existingUser = new User();
        existingUser.setName("Zsombor");
        existingUser.setEmail(inUseEmail);
        existingUser.setAdmin(false);
        existingUser.setPassword("password");

        // when
        userRepository.save(existingUser);
        userRepository.save(user);

        // then
        mockMvc.perform(put("/api/users/update/{modifierUserId}" +
                        "?userIdToModify=1&name=AnotherName&email=anotheremail@gmail.com", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE));
    }

    @Test
    void shouldNotUpdate_User_EmailAlreadyInUse_And_NameIsNull() throws Exception {
        // given
        String originalEmail = "someemail@gmail.com";
        String inUseEmail = "anotheremail@gmail.com";
        String originalName = "Zsombor";
        String password = "password";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword(password);

        User existingUser = new User();
        existingUser.setName(originalName);
        existingUser.setEmail(inUseEmail);
        existingUser.setAdmin(false);
        existingUser.setPassword(password);

        // when
        userRepository.save(existingUser);
        userRepository.save(user);

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
        user.setName(originalName);
        user.setEmail(originalEmail);
        user.setAdmin(false);
        user.setPassword(password);

        // when
        userRepository.save(user);

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
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        userRepository.save(user);

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
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        userRepository.save(user);

        //then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"anotheremail@gmail.com\"," +
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
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        userRepository.save(user);

        //then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"email\":\"anotherEmail@gmail.com\"," +
                                "\"password\": \"123\"" +
                                "}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthException))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD));
    }
}
