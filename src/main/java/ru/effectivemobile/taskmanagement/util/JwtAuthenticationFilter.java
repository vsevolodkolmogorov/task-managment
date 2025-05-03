package ru.effectivemobile.taskmanagement.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.effectivemobile.taskmanagement.service.impl.JwtServiceImpl;
import ru.effectivemobile.taskmanagement.service.impl.UserDetailsServiceImpl;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Injecting JwtService and UserDetailsService to handle token validation and user details fetching.
    private final JwtServiceImpl jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * This method is executed once per request to filter incoming HTTP requests
     * and check for the presence and validity of the JWT in the Authorization header.
     * If valid, it sets up the authentication context for the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Extracting the "Authorization" header from the incoming request.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Check if the Authorization header is present and starts with "Bearer ".
        // If not, the request passes through without any authentication.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token by removing the "Bearer " prefix.
        jwt = authHeader.substring(7);

        // Extract the username (email) from the JWT token.
        userEmail = jwtService.extractUsername(jwt);

        // If the email is extracted and the user is not already authenticated, validate the token.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details using the email extracted from the JWT.
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userEmail);

            // If the token is valid, create a new authentication token and set it in the SecurityContext.
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Create a new authentication token with the user details and their authorities.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // Password is null because authentication is done through the JWT.
                        userDetails.getAuthorities()  // User's authorities (roles/permissions).
                );

                // Set the details (like IP address) for the authentication token.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication token in the SecurityContextHolder.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue the filter chain and allow the request to proceed.
        filterChain.doFilter(request, response);
    }
}
