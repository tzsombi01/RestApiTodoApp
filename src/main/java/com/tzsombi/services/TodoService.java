package com.tzsombi.services;

import com.tzsombi.exceptions.TodoNotFoundException;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.Todo;
import com.tzsombi.repositories.TodoRepository;
import com.tzsombi.repositories.UserRepository;
import com.tzsombi.utils.CredentialChecker;
import com.tzsombi.utils.ErrorConstants;
import com.tzsombi.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    private final UserRepository userRepository;

    private final Logger logger;

    @Autowired
    public TodoService(TodoRepository todoRepository, UserRepository userRepository, Logger logger) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public void addTodo(Long modifierUserId, Todo todo) throws UserNotFoundException, IOException {
        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify));
        }

        if(todo.getDueDate().isBefore(LocalDateTime.now())) {
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            throw new IllegalArgumentException(
                    String.format(ErrorConstants.TODO_DUE_DATE_MUST_BE_GREATER_ERROR_MESSAGE, formattedDateTime));
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        todoRepository.save(todo);

        String logLine = String.join(",",
                "add todo",
                modifierUserId.toString(),
                todo.getId().toString(),
                LocalDateTime.now().toString());
        logger.convertDataToCSvAndWriteToFile(logLine);
    }

    public List<Todo> getAllTodosOfUser(Long userId) {
        return todoRepository.findAllByUserId(userId);
    }

    @Transactional
    public void updateTodo(Long modifierUserId, Long todoId, String title,
                           String description, LocalDateTime dueDate, Boolean completed) throws IOException {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException(
                        String.format(ErrorConstants.TODO_NOT_FOUND_MESSAGE, todoId)));

        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify));
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        if(title != null && !Objects.equals(todo.getTitle(), title)) {
            todo.setTitle(title);
        }

        if(description != null && !Objects.equals(todo.getDescription(), description)) {
            todo.setDescription(description);
        }

        if(dueDate != null && dueDate.isAfter(LocalDateTime.now())
                && !Objects.equals(todo.getDueDate(), dueDate)) {
            todo.setDueDate(dueDate);
            todo.setNotified(false);
        }

        if(completed != null && !Objects.equals(todo.isCompleted(), completed)) {
            todo.setCompleted(completed);
        }

        String logLine = String.join(",",
                "update todo",
                modifierUserId.toString(),
                userIdToModify.toString(),
                LocalDateTime.now().toString());
        logger.convertDataToCSvAndWriteToFile(logLine);
    }

    public void deleteTodoById(Long modifierUserId, Long todoId) throws TodoNotFoundException, IOException {
        Todo todo = todoRepository.findById(todoId)
                        .orElseThrow(() -> new TodoNotFoundException(
                                String.format(ErrorConstants.TODO_NOT_FOUND_MESSAGE, todoId)));

        Long userIdToModify = todo.getUserId();
        if(!userRepository.existsById(userIdToModify)) {
            throw new UserNotFoundException(String.format(ErrorConstants.USER_NOT_FOUND_MESSAGE, userIdToModify));
        }

        CredentialChecker.checkCredentialsOfModifierUser(modifierUserId, userIdToModify, userRepository);

        todoRepository.deleteById(todoId);

        String logLine = String.join(",",
                "delete todo",
                modifierUserId.toString(),
                todoId.toString(),
                LocalDateTime.now().toString());
        logger.convertDataToCSvAndWriteToFile(logLine);
    }
}
