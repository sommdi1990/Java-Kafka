package com.kafka.gateway.controller;

import com.kafka.gateway.dto.User;
import com.kafka.gateway.service.JwtService;
import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @LogExecution
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // In a real implementation, you would validate against database
        if (isValidUser(username, password)) {
            User user = createSampleUser(username);
            String token = jwtService.generateToken(user);

            Map<String, Object> response = Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "roles", user.getRoles()
                    ),
                    "expiresAt", LocalDateTime.now().plusHours(24)
            );

            return ApiResponse.success("Login successful", response);
        } else {
            return ApiResponse.error("Invalid credentials");
        }
    }

    @PostMapping("/register")
    @LogExecution
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String email = registerRequest.get("email");
        String password = registerRequest.get("password");

        // In a real implementation, you would save to database
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(registerRequest.get("firstName"))
                .lastName(registerRequest.get("lastName"))
                .roles(Set.of(User.Role.USER))
                .build();

        String token = jwtService.generateToken(user);

        Map<String, Object> response = Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", user.getRoles()
                ),
                "expiresAt", LocalDateTime.now().plusHours(24)
        );

        return ApiResponse.success("Registration successful", response);
    }

    @PostMapping("/logout")
    @LogExecution
    public ApiResponse<String> logout(@RequestHeader("Authorization") String token) {
        // In a real implementation, you would invalidate the token
        return ApiResponse.success("Logout successful", "Token invalidated");
    }

    @GetMapping("/validate")
    @LogExecution
    public ApiResponse<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String username = jwtService.extractUsername(cleanToken);

            Map<String, Object> response = Map.of(
                    "valid", true,
                    "username", username,
                    "message", "Token is valid"
            );

            return ApiResponse.success("Token validation successful", response);
        } catch (Exception e) {
            return ApiResponse.error("Invalid token");
        }
    }

    private boolean isValidUser(String username, String password) {
        // Simple validation - in real implementation, check against database
        return "admin".equals(username) && "admin123".equals(password) ||
                "user".equals(username) && "user123".equals(password);
    }

    private User createSampleUser(String username) {
        return User.builder()
                .id(1L)
                .username(username)
                .email(username + "@example.com")
                .firstName("Sample")
                .lastName("User")
                .roles(Set.of(User.Role.USER))
                .isActive(true)
                .isLocked(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
