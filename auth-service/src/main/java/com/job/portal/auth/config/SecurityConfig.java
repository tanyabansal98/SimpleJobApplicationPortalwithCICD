package com.job.portal.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig is our Security Configuration layer.
 * Analogy: The security guard protocols manual. It specifies which doors are locked, 
 * which doors are open to the public, and how passwords should be encrypted.
 *
 * @Configuration: Tells Spring that this is a setup/configuration class.
 * @EnableWebSecurity: Activates Spring Security web protection filters.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Exposes the BCryptPasswordEncoder as a Spring Bean.
     * Analogy: The official password shredder. 
     * BCrypt is a one-way hashing function. We use it to turn a user's plain-text password 
     * into an unreadable, secure string.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets up the security filter rules for HTTP requests.
     * We keep it permissive for now so we can test endpoints.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable Cross-Site Request Forgery (CSRF). Since we are using stateless JWTs 
            // instead of browser cookies, we don't need CSRF cookie protection.
            .csrf(csrf -> csrf.disable())
            
            // Define who is allowed to visit which URL path.
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access any endpoint on our service for now.
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
