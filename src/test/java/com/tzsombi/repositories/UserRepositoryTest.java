package com.tzsombi.repositories;

import com.tzsombi.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTestUserRepository;

    @AfterEach
    void tearDown() {
        underTestUserRepository.deleteAll();
    }

    @Test
    void shouldReturn_UserByEmail() {
        // given
        String email = "someemail@gmail.com";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword("password");
        underTestUserRepository.save(user);

        // when
        User userFound = underTestUserRepository.findByEmail(email)
                .orElse(null);

        // then
        assertThat(userFound).isEqualTo(user);
    }

    @Test
    void shouldNotReturn_UserByEmail() {
        // given
        String email = "someemail@gmail.com";

        // when
        User userFound = underTestUserRepository.findByEmail(email)
                .orElse(null);

        // then
        assertThat(userFound).isEqualTo(null);
    }

    @Test
    void shouldReturn_UserExistsByEmail() {
        // given
        String email = "someemail@gmail.com";
        User user = new User();
        user.setName("Zsombor");
        user.setEmail(email);
        user.setAdmin(false);
        user.setPassword("password");
        underTestUserRepository.save(user);

        // when
        boolean exists = underTestUserRepository.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldNotReturn_UserExistsByEmail() {
        // given
        String email = "someemail@gmail.com";

        // when
        boolean exists = underTestUserRepository.existsByEmail(email);

        // then
        assertThat(exists).isFalse();
    }

}