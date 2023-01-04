package com.tzsombi.controllers;

import com.tzsombi.model.Todo;
import com.tzsombi.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    TodoService todoService;

    @PostMapping("/add/{modifierUserId}")
    public ResponseEntity<String> addTodo(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestBody Todo todo) {
        todoService.addTodo(modifierUserId, todo);
        return new ResponseEntity<>("Successfully added Todo!", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos(@RequestParam Long userId) {
        List<Todo> todos = todoService.getAllTodosOfUser(userId);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PutMapping("/update/{modifierUserId}")
    public ResponseEntity<String> updateTodo(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestParam Long todoId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) LocalDateTime dueDate,
            @RequestParam(required = false) Boolean completed) {
        todoService.updateTodo(modifierUserId, todoId, title, description, dueDate, completed);
        return new ResponseEntity<>("Todo updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{modifierUserId}")
    public ResponseEntity<String> deleteTodoById(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestParam Long todoId) {
        todoService.deleteTodoById(modifierUserId, todoId);
        return new ResponseEntity<>("Todo got deleted successfully!", HttpStatus.OK);
    }
}
