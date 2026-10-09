package com.job.portal.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Tells Spring to activate Spring's WebSecurity config which allows every
                   // incoming http request to pass through filters(authentication, authorization,
                   // CSRF and Session Handling) before any request reaches Controllers. and
                   // @Configuration lets us customise the filters
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            if (req.getServletPath().startsWith("/api/")) {
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            } else {
                                res.sendRedirect(req.getContextPath() + "/");
                            }
                        }))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD,
                                jakarta.servlet.DispatcherType.INCLUDE)
                        .permitAll()
                        .requestMatchers("/", "/login", "/register", "/forgot-password", "/ping",
                                "/css/**", "/js/**",
                                "/images/**", "/WEB-INF/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/employer/**").hasRole("EMPLOYER")
                        .anyRequest().authenticated())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}