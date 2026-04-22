package com.blog.userservice.controller;

import com.blog.userservice.dto.UserRoleUpdateRequest;
import com.blog.userservice.model.User;
import com.blog.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Import List

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can access this controller
public class AdminController {

    private final UserService userService;

    @PutMapping("/update-role")
    public ResponseEntity<User> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
        User updatedUser = userService.updateUserRole(request.getUserId(), request.getNewRole());
        return ResponseEntity.ok(updatedUser);
    }

    // New endpoint to get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}
