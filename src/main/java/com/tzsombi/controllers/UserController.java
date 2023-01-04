package com.tzsombi.controllers;

import com.tzsombi.exceptions.AuthException;
import com.tzsombi.exceptions.UserNotFoundException;
import com.tzsombi.model.User;
import com.tzsombi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user)
            throws UserNotFoundException, AuthException, IOException {
        userService.registerUser(user);
        return new ResponseEntity<>("Registered Successfully!", HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{deleterUserId}")
    public ResponseEntity<String> deleteUserById(
            @PathVariable("deleterUserId") Long deleterUserId,
            @RequestParam(required = true)  Long userIdToDelete) {
        userService.deleteUserById(deleterUserId, userIdToDelete);
        return new ResponseEntity<>("User got deleted successfully!", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update/{modifierUserId}")
    public ResponseEntity<String> updateUser(
            @PathVariable("modifierUserId") Long modifierUserId,
            @RequestParam(required = true)  Long userIdToModify,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        userService.updateUser(modifierUserId, userIdToModify, name, email);
        return new ResponseEntity<>("User updated successfully!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        userService.validateUser(email, password);
        return new ResponseEntity<>("Login successful", HttpStatus.ACCEPTED);
    }
}
