package com.tzsombi.services;

import com.tzsombi.exceptions.TodoNotFoundException;
import com.tzsombi.model.Todo;
import com.tzsombi.model.User;
import com.tzsombi.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void addTodo(Todo todo) {
        todoRepository.save(todo);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @Transactional
    public void updateTodo(Long todoId, String title, String description, Boolean completed) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("No Todo found with id: " + todoId + "!"));
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

    public void deleteTodoById(Long todoId) throws TodoNotFoundException {
        if(!todoRepository.existsById(todoId)) {
            throw new TodoNotFoundException("No todo found with ID: " + todoId + "!");
        }
        todoRepository.deleteById(todoId);
    }
}
