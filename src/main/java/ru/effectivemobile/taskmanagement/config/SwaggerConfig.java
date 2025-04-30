package ru.effectivemobile.taskmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration class for setting up the OpenAPI documentation.
 * <p>
 * This class configures the OpenAPI specification used by Swagger to generate and document the API.
 * It includes the security scheme for JWT-based authentication (Bearer token).
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configures and returns the custom OpenAPI instance for the application.
     * <p>
     * This method defines the security scheme for Bearer JWT authentication. It ensures that
     * all API endpoints will be properly secured and the Swagger UI will know how to handle
     * authentication using JWT tokens.
     * </p>
     *
     * @return a customized OpenAPI instance with JWT security scheme.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Define the security scheme name used for authentication
        final String securitySchemeName = "bearerAuth";

        // Create and return the OpenAPI instance with security settings
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))  // Apply the security requirement globally
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,  // Define the security scheme used in the API
                                new SecurityScheme()
                                        .name(securitySchemeName)  // The name of the security scheme
                                        .type(SecurityScheme.Type.HTTP)  // HTTP-based authentication
                                        .scheme("bearer")  // Use bearer token
                                        .bearerFormat("JWT")  // Specify the format (JWT) for the bearer token
                        )
                );
    }
}
