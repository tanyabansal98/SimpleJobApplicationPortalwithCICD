package com.job.portal.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of our Authentication microservice.
 * 
 * We removed the exclusions of DataSourceAutoConfiguration, HibernateJpaAutoConfiguration,
 * and SecurityAutoConfiguration because we are now connecting to the Oracle Database 
 * and configuring our security rules in SecurityConfig.java.
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
