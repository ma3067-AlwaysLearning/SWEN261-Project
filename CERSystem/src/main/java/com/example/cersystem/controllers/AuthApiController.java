package com.example.cersystem.controllers;

import com.example.cersystem.models.User;
import com.example.cersystem.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserRepository userRepository;

    public AuthApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Simple request DTO for Angular JSON login
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of(
                "headerName", token.getHeaderName(),
                "parameterName", token.getParameterName(),
                "token", token.getToken()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Not authenticated"));
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        User user = userOpt.get();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", user.getName()); // change if your getter is different
        response.put("email", user.getEmail());
        response.put("university_id", user.getUniversity_id()); // change if your getter is different
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body, HttpServletRequest request) {
        if (body == null || body.getEmail() == null || body.getPassword() == null
                || body.getEmail().isBlank() || body.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password are required"));
        }

        try {
            // Uses your existing Spring Security + UserDetailsService login flow
            request.login(body.getEmail(), body.getPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful"
            ));
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            request.logout();
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Logout failed"));
        }
    }
}