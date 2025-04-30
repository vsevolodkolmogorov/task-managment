package ru.effectivemobile.taskmanagement.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.taskmanagement.dto.AuthResponseDTO;
import ru.effectivemobile.taskmanagement.dto.LoginRequestDTO;
import ru.effectivemobile.taskmanagement.dto.RegisterRequestDTO;
import ru.effectivemobile.taskmanagement.service.impl.AuthServiceImpl;

/**
 * Controller responsible for handling authentication operations such as user registration and login.
 * Provides endpoints for issuing JWT tokens.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * Constructor injection of authentication service.
     *
     * @param authService the authentication service implementation
     */
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user in the system.
     *
     * @param registerDTO the registration request containing email and password
     * @return a response with a generated JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerDTO) {
        return ResponseEntity.ok(authService.register(registerDTO));
    }

    /**
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param loginDTO the login request containing email and password
     * @return a response with a JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }
}
