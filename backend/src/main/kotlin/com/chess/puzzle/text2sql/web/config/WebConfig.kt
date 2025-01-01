package com.chess.puzzle.text2sql.web.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configuration class for web-related settings in the Spring application.
 *
 * This class implements [WebMvcConfigurer] to customize Spring MVC behavior, such as configuring
 * Cross-Origin Resource Sharing (CORS) settings.
 *
 * For more information on Spring MVC configuration, refer to the official Spring Framework
 * documentation.
 */
@Configuration
class WebConfig : WebMvcConfigurer {

    /**
     * Configures Cross-Origin Resource Sharing (CORS) mappings for the application.
     *
     * This method allows the application to handle CORS requests by specifying allowed origins,
     * methods, headers, and credentials. This configuration applies to all endpoints and allows
     * requests from specific origins.
     *
     * @param registry The [CorsRegistry] used to configure CORS mappings.
     */
    // IMPORTANT: Be cautious when configuring CORS settings. Allowing all origins or headers
    // can expose the application to security risks. Ensure that only trusted origins are allowed.
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            // http://localhost:3000 correspond to the frontend
            // http://localhost:8000 correspond to the fastapi
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost:8000",
                "http://frontend",
                "http://fastapi",
            )
            .allowedMethods("GET", "POST")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
