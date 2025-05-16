package ru.effectivemobile.taskmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.effectivemobile.taskmanagement.service.impl.UserDetailsServiceImpl;
import ru.effectivemobile.taskmanagement.util.JwtAuthenticationFilter;

import java.util.List;

/**
 * Security configuration class for the application.
 * <p>
 * Configures Spring Security for HTTP request authorization, authentication mechanisms,
 * and JWT-based token authentication. It also includes password encoding and form login
 * settings.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // List of whitelisted URLs that are publicly accessible without authentication.
    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/webjars/**",
            "auth/login",
            "/",
            "auth/register",
            "/auth/**",
            "/auth/me"
    };

    /**
     * Configures the security filter chain.
     * <p>
     * This method configures the security settings for HTTP requests. It defines which
     * URLs are publicly accessible (whitelisted), which ones require authentication,
     * and which ones require specific roles (e.g., ADMIN). It also configures the
     * JWT filter to intercept requests before they reach the authentication filter.
     * </p>
     *
     * @param http HttpSecurity object to configure security settings.
     * @return A configured SecurityFilterChain.
     * @throws Exception if an error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)  // Disable CSRF protection (since we're using JWT)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorization -> authorization
                        // Publicly accessible URLs
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        // Task-related endpoints requiring authentication
                        .requestMatchers(HttpMethod.GET, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").authenticated()
                        // Admin-only task-related endpoints
                        .requestMatchers(HttpMethod.POST, "/tasks/admin").hasRole("ADMIN")
                        // Task endpoints available to both ADMIN and USER
                        .requestMatchers("/tasks/**").hasAnyRole("ADMIN", "USER")
                        // Any other requests require authentication
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())  // Set the authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)  // Add the JWT filter before the username/password authentication filter
                .formLogin(AbstractHttpConfigurer::disable)  // Disable form login (using JWT instead)
                .httpBasic(AbstractHttpConfigurer::disable);  // Disable HTTP basic authentication (using JWT instead)

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Defines the AuthenticationProvider to authenticate users based on their credentials.
     * <p>
     * The provider uses the UserDetailsService and a password encoder to authenticate users.
     * </p>
     *
     * @return The configured AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);  // Set the UserDetailsService
        authenticationProvider.setPasswordEncoder(passwordEncoder());  // Set the password encoder
        return authenticationProvider;
    }

    /**
     * Configures the password encoder used for encoding and matching passwords.
     * <p>
     * This method returns a BCryptPasswordEncoder instance which is used to hash passwords
     * and compare them during authentication.
     * </p>
     *
     * @return A configured PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCrypt for password encoding
    }

    /**
     * Configures the AuthenticationManager for the application.
     * <p>
     * This bean is used by Spring Security for managing authentication requests and
     * checking credentials.
     * </p>
     *
     * @param config AuthenticationConfiguration instance to configure the manager.
     * @return A configured AuthenticationManager.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();  // Get the authentication manager from the configuration
    }
}
