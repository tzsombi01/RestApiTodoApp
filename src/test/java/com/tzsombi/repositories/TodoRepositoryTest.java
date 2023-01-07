package com.tzsombi.repositories;

import com.tzsombi.model.Todo;
import com.tzsombi.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository underTestTodoRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn_AllTodos_UnderUserById() {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        Todo todo = new Todo();
        todo.setTitle("My Title");
        todo.setDescription("Description");
        todo.setUserId(savedUser.getId());
        todo.setDueDate(LocalDateTime.now(Clock.systemDefaultZone()).plusHours(1));
        todo.setCompleted(false);
        Todo savedTodo = underTestTodoRepository.save(todo);

        // when
        List<Todo> foundTodos = underTestTodoRepository.findAllByUserId(savedUser.getId());

        // then
        assertThat(foundTodos).isEqualTo(List.of(savedTodo));
    }

    @Test
    void shouldReturn_AnEmptyList_NoTodoUnderUser() {
        // given
        User user = new User();
        user.setName("Zsombor");
        user.setEmail("someemail@gmail.com");
        user.setAdmin(false);
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        // when
        List<Todo> foundTodos = underTestTodoRepository.findAllByUserId(savedUser.getId());

        // then
        assertThat(foundTodos).isEqualTo(List.of());
    }

}