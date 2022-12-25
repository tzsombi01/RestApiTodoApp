package com.tzsombi.resources;

import com.tzsombi.model.User;
import com.tzsombi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return new ResponseEntity<>("Registered Successfully!", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable("userId") Integer userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>("User got deleted successfully!", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable("userId") Integer userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String profilePictureUrl) {
        userService.updateUser(userId, name, email, profilePictureUrl);
        return new ResponseEntity<>("User updated!", HttpStatus.OK);
    }
}
