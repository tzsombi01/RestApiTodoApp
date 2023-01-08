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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userServiceUnderTest;

    private final UserRepository userRepository;

    @Mock private Logger logger;

    @Mock private EmailSendingObserver emailSendingObserver;

    @Autowired
    UserServiceTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userServiceUnderTest = new UserService(userRepository, logger, emailSendingObserver);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void canRegister_User() throws IOException {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        // when
        userServiceUnderTest.registerUser(user);

        // then
        assertThat(userRepository.existsByEmail("someemail@gmail.com")).isTrue();
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

        User user2 = new User();
        user2.setName("Zsombor");
        user2.setEmail(email);
        user2.setAdmin(false);
        user2.setPassword("password");
        userRepository.save(user);

        // when
        // then
        assertThatThrownBy(() -> userServiceUnderTest.registerUser(user2))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.EMAIL_IS_ALREADY_IN_USE);
    }

    @Test
    void whenGetAllUsers_FindAll() {
        // then
        assertThat(userServiceUnderTest.getAllUsers()).isEqualTo(List.of());
    }

    @Test
    void canDeleteUser_ByAdmin() throws IOException {
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

        // when
        User admin = userRepository.save(user1);
        User userToDelete = userRepository.save(user2);
        userServiceUnderTest.deleteUserById(admin.getId(), userToDelete.getId());

        // then
        assertThat(userRepository.findById(2L)).isEqualTo(Optional.empty());
    }

    @Test
    void canDeleteUser_BySelf() throws IOException {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");

        // when
        User normalUser = userRepository.save(user);
        userServiceUnderTest.deleteUserById(normalUser.getId(), normalUser.getId());

        // then
        assertThat(userRepository.findById(1L)).isEqualTo(Optional.empty());
    }

    @Test
    void canNotDelete_AnotherUser_ByNotAdmin() {
        // given
        User user1 = new User();
        user1.setName("Zsombor");
        user1.setEmail("someemail@gmail.com");
        user1.setAdmin(true);
        user1.setPassword("password");

        User user2 = new User();
        user2.setName("Zsombor");
        user2.setEmail("someemail2@gmail.com");
        user2.setAdmin(false);
        user2.setPassword("password");

        // when
        User admin = userRepository.save(user1);
        User userToDelete = userRepository.save(user2);

        // then
        assertThatThrownBy(() -> userServiceUnderTest.deleteUserById(userToDelete.getId(), admin.getId()))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.NO_PERMISSION_TO_MODIFY_USER);
    }

    @Test
    void can_Update_User() throws IOException {
        // given
        String replacingName = "Another Name";
        String replacingEmail = "anotheremail@gmail.com";

        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(true);
        user.setPassword("password");

        userRepository.save(user);
        // when
        // then
        User updatedUser = userServiceUnderTest.updateUser(user.getId(), user.getId(), replacingName, replacingEmail);
        assertThat(updatedUser.getName()).isEqualTo(replacingName);
        assertThat(updatedUser.getEmail()).isEqualTo(replacingEmail);
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

        userRepository.save(user);
        // when
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

        userRepository.save(user);
        // when
        // then
        assertThatThrownBy(() -> userServiceUnderTest.validateUser("asd" + email, password))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining(ErrorConstants.INVALID_EMAIL_OR_PASSWORD);
    }
}