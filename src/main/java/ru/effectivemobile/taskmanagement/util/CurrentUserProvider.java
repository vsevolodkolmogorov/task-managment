package ru.effectivemobile.taskmanagement.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.effectivemobile.taskmanagement.model.User;
import ru.effectivemobile.taskmanagement.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    // Injecting the UserRepository to fetch the current user from the database.
    private final UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the Security Context.
     * This method assumes that the user is already authenticated and their
     * information is available in the Security Context.
     *
     * @return The currently authenticated User object.
     * @throws RuntimeException If no user is found in the database with the email from the Security Context.
     */
    public User getCurrentUser() {
        // Retrieve the current user's email from the SecurityContext.
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user from the database using the email and return the user object.
        // If the user does not exist, throw an exception.
        return userRepository.findByEmail(email).orElseThrow();
    }
}
