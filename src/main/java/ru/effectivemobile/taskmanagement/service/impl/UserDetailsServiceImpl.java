package ru.effectivemobile.taskmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * Loads user details by the username (email in this case).
     * This method is used by Spring Security during authentication.
     *
     * @param email The email of the user.
     * @return UserDetails A Spring Security UserDetails object containing user information.
     * @throws UsernameNotFoundException If no user is found with the provided email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user with email: {}", email);

        // Retrieve the user from the repository using the email.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email " + email);
                });

        // Log successful retrieval of user details.
        logger.info("Successfully loaded user with email: {}", email);

        // Return a Spring Security User object, setting the username, password, and role.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),  // The username (email) of the user.
                user.getPassword(),  // The encoded password of the user.
                // A user's role is assigned as a granted authority. "ROLE_" is prepended to the role name.
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}