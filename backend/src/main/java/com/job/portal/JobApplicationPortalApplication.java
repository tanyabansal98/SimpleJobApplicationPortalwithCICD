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
		SpringApplication.run(JobApplicationPortalApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedAdmin(UserService userService) {
		return args -> {
			try {
				// Seed a Superuser Admin account for the presentation
				String adminEmail = "admin@portal.com";
				try {
					userService.findByEmail(adminEmail);
				} catch (Exception e) {
					userService.register(adminEmail, "Admin123", Role.ADMIN);
					System.out.println(">>> SEEDED ADMIN: " + adminEmail + " (Pass: Admin123)");
				}

				// Seed a Presentation Student account
				String studentEmail = "student@portal.com";
				try {
					userService.findByEmail(studentEmail);
				} catch (Exception e) {
					userService.register(studentEmail, "Student123", Role.STUDENT);
					System.out.println(">>> SEEDED STUDENT: " + studentEmail + " (Pass: Student123)");
				}

				// Seed a Presentation Employer account
				String employerEmail = "employer@portal.com";
				try {
					userService.findByEmail(employerEmail);
				} catch (Exception e) {
					userService.register(employerEmail, "Employer123", Role.EMPLOYER);
					System.out.println(">>> SEEDED EMPLOYER: " + employerEmail + " (Pass: Employer123)");
				}
			} catch (Exception e) {
				// Silent fail if seeding has issues
			}
		};
	}

}
