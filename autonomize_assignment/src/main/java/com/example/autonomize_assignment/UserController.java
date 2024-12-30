package com.example.autonomize_assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/{username}")
    public ResponseEntity<User> saveUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.saveOrUpdateUser(username));
    }

    @GetMapping("/{username}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable String username) {
        return ResponseEntity.ok(userService.getFriends(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable String username) {
        userService.softDeleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}")
    public ResponseEntity<Void> updateUser(@PathVariable String username, @RequestBody Map<String, Object> updates) {
        userService.updateUser(username, updates);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<User>> getAllUsersSorted(@RequestParam String field) {
        return ResponseEntity.ok(userService.getAllUsersSorted(field));
    }
}

