package com.yourcaryourway.chat_backend.controllers;

import com.yourcaryourway.chat_backend.models.dto.UserDTO;
import com.yourcaryourway.chat_backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDTO(
                        user.getUserId(),
                        user.getFirstName(),
                        user.getLastName()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
