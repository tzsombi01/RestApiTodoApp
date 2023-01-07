package com.tzsombi.services;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.model.User;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.ErrorConstants;
import com.tzsombi.utils.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userServiceUnderTest;

    @Mock private UserRepository userRepository;

    @Mock private Logger logger;

    @Mock private EmailSendingObserver userEmailObserver;

    @BeforeEach
    void setUp() {
        userServiceUnderTest = new UserService(userRepository, logger, userEmailObserver);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void canRegister_User() {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        // when
        userServiceUnderTest.registerUser(user);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void willThrow_EmailIsInvalid() {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemailgmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        // when
        // then
        assertThatThrownBy(() -> userServiceUnderTest.registerUser(user))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.INVALID_EMAIL_FORMAT);
    }

    @Test
    void canNotRegisterUser_EmailIsTaken() {
        // given
        String email = "someemail@gmail.com";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword("password");

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userServiceUnderTest.registerUser(user))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE);
    }

    @Test
    void whenGetAllUsers_FindAllIsInvoked() {
        // when
        userServiceUnderTest.getAllUsers();

        // then
        verify(userRepository).findAll();
    }

    @Test
    void canDeleteUser_ByAdmin() {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Zsombor");
        user1.setEmail("someemail@gmail.com");
        user1.setAdmin(true);
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Zsombor");
        user2.setEmail("someemail2@gmail.com");
        user2.setAdmin(false);
        user2.setPassword("password");

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        // when
        User admin = userRepository.save(user1);
        User userToDelete = userRepository.save(user2);
        userServiceUnderTest.deleteUserById(admin.getId(), userToDelete.getId());

        // then
        verify(userRepository).delete(userToDelete);
    }

    @Test
    void canDeleteUser_BySelf() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        User normalUser = userRepository.save(user);
        userServiceUnderTest.deleteUserById(normalUser.getId(), normalUser.getId());

        // then
        verify(userRepository).delete(normalUser);
    }

    @Test
    void canNotDelete_AnotherUser_ByNotAdmin() {
        // given
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Zsombor");
        user1.setEmail("someemail@gmail.com");
        user1.setAdmin(true);
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Zsombor");
        user2.setEmail("someemail2@gmail.com");
        user2.setAdmin(false);
        user2.setPassword("password");

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        // when
        User admin = userRepository.save(user1);
        User userToDelete = userRepository.save(user2);

        // then
        assertThatThrownBy(() -> userServiceUnderTest.deleteUserById(userToDelete.getId(), admin.getId()))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.NO_PERMISSION_TO_MODIFY_USER);
    }

    @Test
    void can_Update_User() {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(true);
        user.setPassword("password");

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // then
        String replacingName = "Another Name";
        String replacingEmail = "anotheremail@gmail.com";
        userServiceUnderTest.updateUser(user.getId(), user.getId(), replacingName, replacingEmail);
        assertThat(user.getName()).isEqualTo(replacingName);
        assertThat(user.getEmail()).isEqualTo(replacingEmail);
    }

    @Test
    void canNotValidateUserBecausePassword() {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(true);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> userServiceUnderTest.validateUser(email, password + "asd"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD);
    }

    @Test
    void canNotValidateUserBecauseEmail() {
        // given
        String email = "someemail@gmail.com";
        String password = "password";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(true);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));

        // when
        // then
        assertThatThrownBy(() -> userServiceUnderTest.validateUser("asd" + email, password))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD);
    }
}