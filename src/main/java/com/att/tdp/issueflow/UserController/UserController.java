package com.att.tdp.issueflow.controllers;

import com.att.tdp.issueflow.entities.User;
import com.att.tdp.issueflow.services.CommentService;
import com.att.tdp.issueflow.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // as request - POST /users/update/:userId
    @PostMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updateRequest) {
        return ResponseEntity.ok(userService.updateUser(userId, updateRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{userId}/mentions")
    public ResponseEntity<List<com.att.tdp.issueflow.entities.Comment>> getUserMentions(@PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getMentionsForUser(userId));
    }
}