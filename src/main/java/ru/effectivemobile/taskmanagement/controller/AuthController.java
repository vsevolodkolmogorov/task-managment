package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.taskmanagement.dto.AuthResponseDTO;
import ru.effectivemobile.taskmanagement.dto.LoginRequestDTO;
import ru.effectivemobile.taskmanagement.dto.RegisterRequestDTO;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.service.impl.AuthServiceImpl;
import ru.effectivemobile.taskmanagement.service.impl.JwtServiceImpl;
import ru.effectivemobile.taskmanagement.util.CurrentUserProvider;

import java.util.Map;

/**
 * Controller responsible for handling authentication operations such as user registration and login.
 * Provides endpoints for issuing JWT tokens.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthServiceImpl authService;
    private final JwtServiceImpl jwtService;

    /**
     * Registers a new user in the system.
     *
     * @param registerDTO the registration request containing email and password
     * @return a response with a generated JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        log.info("Register request received for email: {}", registerDTO.getEmail());
        AuthResponseDTO response = authService.register(registerDTO);
        log.info("User registered successfully: {}", registerDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param loginDTO the login request containing email and password
     * @return a response with a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        log.info("Login attempt for email: {}", loginDTO.getEmail());
        AuthResponseDTO response = authService.login(loginDTO);
        log.info("Login successful for email: {}", loginDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        User user = authService.getCurrentUser(token);
        return ResponseEntity.ok(new AuthResponseDTO(token, user));
    }
}
