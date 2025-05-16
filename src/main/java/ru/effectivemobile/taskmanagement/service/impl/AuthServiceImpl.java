package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.taskmanagement.dto.AuthResponseDTO;
import ru.effectivemobile.taskmanagement.dto.LoginRequestDTO;
import ru.effectivemobile.taskmanagement.dto.RegisterRequestDTO;
import ru.effectivemobile.taskmanagement.exceptions.EmailAlreadyRegisteredException;
import ru.effectivemobile.taskmanagement.model.Role;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.repository.UserRepository;
import ru.effectivemobile.taskmanagement.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     * <p>
     * This method validates the registration request, encodes the password, and saves the new user in the repository.
     * After the user is saved, a JWT token is generated and returned.
     * </p>
     *
     * @param request the registration request containing user details
     * @return an AuthResponseDTO containing the JWT token
     */
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Validate if email is already registered
        validateRegister(request);

        // Build and save the new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Password encryption
                .role(Role.USER)  // Default role for a new user
                .build();

        userRepository.save(user);  // Save the user in the database

        // Generate a JWT token after successful registration
        String jwt = jwtService.generateToken(user);

        // Log successful registration
        logger.info("User registered successfully with email: {}", request.getEmail());

        return new AuthResponseDTO(jwt, user);  // Return the token
    }

    /**
     * Authenticates a user by email and password.
     * <p>
     * This method authenticates the user using the provided credentials. If successful, it generates and returns a JWT token.
     * </p>
     *
     * @param request the login request containing user credentials
     * @return an AuthResponseDTO containing the JWT token
     */
    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Authenticate user with the provided email and password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            // Log failed login attempt for diagnostics
            logger.warn("Login failed for email: {}", request.getEmail());
            throw new UsernameNotFoundException("Invalid email or password");
        }

        // Find user by email after successful authentication
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User with email not found: " + request.getEmail()));

        // Generate JWT token after successful login
        String jwt = jwtService.generateToken(user);

        // Log successful login
        logger.info("User logged in successfully with email: {}", request.getEmail());

        return new AuthResponseDTO(jwt, user);  // Return the token
    }

    @Override
    public User getCurrentUser(String token) {
        String email = jwtService.extractUsername(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email not found: " + email));
    }

    /**
     * Validates if the email is already registered in the system.
     *
     * @param request the registration request containing the user's email
     * @throws EmailAlreadyRegisteredException if the email is already registered
     */
    private void validateRegister(RegisterRequestDTO request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new EmailAlreadyRegisteredException("Email is already registered: " + request.getEmail());
                });
    }
}
