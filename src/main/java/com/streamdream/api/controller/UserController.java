package com.streamdream.api.controller;

import com.streamdream.api.model.User;
import com.streamdream.api.repository.UserRepository;
import com.streamdream.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/update-photo")
    public ResponseEntity<?> updateAvatar(@RequestBody Map<String, String> payload) {
        try {
            Integer userId = Integer.parseInt(payload.get("userId"));
            String base64Image = payload.get("photoUrl");
            User updatedUser = userService.updateAvatar(userId, base64Image);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-bio")
    public ResponseEntity<?> updateBio(@RequestBody Map<String, String> payload) {
        try {
            Integer userId = Integer.parseInt(payload.get("userId"));
            String bio = payload.get("bio");
            User updatedUser = userService.updateBio(userId, bio);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-username")
    public ResponseEntity<?> updateUsername(@RequestBody Map<String, String> payload) {
        try {
            Integer userId = Integer.parseInt(payload.get("userId"));
            String newUsername = payload.get("newUsername");
            User updatedUser = userService.updateUsername(userId, newUsername);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/ban/{id}")
    public ResponseEntity<String> banUser(@PathVariable Integer id) {
        userService.banUser(id);
        return ResponseEntity.ok("User banned!");
    }

    @PutMapping("/unban/{id}")
    public ResponseEntity<String> unbanUser(@PathVariable Integer id) {
        userService.unbanUser(id);
        return ResponseEntity.ok("User unbanned!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}