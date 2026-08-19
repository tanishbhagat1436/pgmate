package com.PGMATE.demo.controller;

import com.PGMATE.demo.model.User;
import com.PGMATE.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginDetails) {
        Optional<User> userOptional = userRepository.findByEmail(loginDetails.get("email"));
        
        if (userOptional.isPresent() && 
            passwordEncoder.matches(loginDetails.get("password"), userOptional.get().getPassword())) {
            
            User user = userOptional.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("role", user.getRole());
            response.put("userId", user.getUserId()); 
            response.put("name", user.getName());     

            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}