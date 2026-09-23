package com.job.portal;

import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class JobApplicationPortalApplication {

	public static void main(String[] args) {
		try {
			System.out.println("In main fucnction, starting the application...");
			SpringApplication.run(JobApplicationPortalApplication.class, args);
		} catch (Exception e) {
			System.err.println("Application failed to start due to an error: " + e.getMessage());
		}
	}

}
