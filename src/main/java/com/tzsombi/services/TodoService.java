package com.tzsombi.services;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.exceptions.TodoNotFoundException;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.Todo;
import com.tzsombi.model.User;
import com.tzsombi.repositories.TodoRepository;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.CredentialChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository,
                       UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public void addTodo(Long modifierUserId, Todo todo) throws UserNotFoundException  {
        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException("No user found with ID: " + userIdToModify + "!");
        }
        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        todoRepository.save(todo);
    }

    public List<Todo> getAllTodosOfUser(Long userId) {
        return todoRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateTodo(Long modifierUserId, Long todoId, String title, String description, Boolean completed) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("No Todo found with id: " + todoId + "!"));

        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException("No user found with ID: " + userIdToModify + "!");
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        if(title != null
                && title.length() > 0
                && !Objects.equals(todo.getTitle(), title)) {
            todo.setTitle(title);
        }
        if(description != null && !Objects.equals(todo.getDescription(), description)) {
            todo.setDescription(description);
        }
        if(completed != null && !Objects.equals(todo.isCompleted(), completed)) {
            todo.setCompleted(completed);
        }
    }

    public void deleteTodoById(Long modifierUserId, Long todoId) throws TodoNotFoundException {
        Todo todo = todoRepository.findById(todoId)
                        .orElseThrow(() -> new TodoNotFoundException("No Todo found with id: " + todoId + "!"));

        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException("No user found with ID: " + userIdToModify + "!");
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        todoRepository.deleteById(todoId);
    }
}
