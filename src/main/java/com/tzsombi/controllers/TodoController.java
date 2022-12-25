package com.tzsombi.controllers;

import com.tzsombi.model.Todo;
import com.tzsombi.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    TodoService todoService;

    @PostMapping("/add")
    public ResponseEntity<String> addTodo(@RequestBody Todo todo) {
        todoService.addTodo(todo);
        return new ResponseEntity<>("Successfully added Todo!", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PutMapping("/update/{todoId}")
    public ResponseEntity<String> updateTodo(
            @PathVariable("todoId") Long todoId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean completed) {
        todoService.updateTodo(todoId, title, description, completed);
        return new ResponseEntity<>("Todo updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{todoId}")
    public ResponseEntity<String> deleteTodoById(@PathVariable("todoId") Long todoId) {
        todoService.deleteTodoById(todoId);
        return new ResponseEntity<>("Todo got deleted successfully!", HttpStatus.OK);
    }
}
