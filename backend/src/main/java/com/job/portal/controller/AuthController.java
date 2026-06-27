package com.job.portal.controller;


import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class AuthController {


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final String authServiceUrl;

    public AuthController(UserService userService, 
                          @Value("${app.auth-service.url:http://localhost:9090}") String authServiceUrl) {
        this.userService = userService;
        this.authServiceUrl = authServiceUrl;
    }

    // Simply shows the initial login screen
    @GetMapping({"/", "/login"})
    public String showLoginPage(Model model) {
        try {
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading login page: " + e.getMessage());
            return "index";
        }
    }

    // A quick check to see if the server is alive
    @GetMapping("/ping")
    @org.springframework.web.bind.annotation.ResponseBody
    public String ping() {
        try {
            return "PONG - Server is running!";
        } catch (Exception e) {
            return "ERROR - Server is experiencing issues: " + e.getMessage();
        }
    }

    // Displays the sign-up page for new students or employers
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        try {
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading registration page: " + e.getMessage());
            return "register";
        }
    }

    // Helper to log in using the external auth-service microservice. Returns null on failure.
    private User tryMicroserviceLogin(String email, String password) {
        log.info("[AUTH] Attempting login via auth-service microservice for user: {}", email);
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Set socket and connection timeouts for quick fallback when the microservice is down
            org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory = 
                    new org.springframework.http.client.SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(1500); // 1.5 seconds connection timeout
            requestFactory.setReadTimeout(1500);    // 1.5 seconds read timeout
            restTemplate.setRequestFactory(requestFactory);

            String url = authServiceUrl + "/auth/login";
            log.info("[AUTH] Calling auth-service at: {}", url);

            java.util.Map<String, String> requestBody = new java.util.HashMap<>();
            requestBody.put("email", email);
            requestBody.put("password", password);

            // Call the microservice
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response = restTemplate.postForObject(url, requestBody, java.util.Map.class);
            if (response != null && response.containsKey("token")) {
                log.info("[AUTH] ✅ Microservice login SUCCEEDED for user: {} | Role: {} | JWT issued",
                        response.get("email"), response.get("role"));
                // If microservice succeeds, fetch the user object from the local DB.
                // This ensures we have a valid Hibernate-managed User object inside the HttpSession.
                return userService.findByEmail(email);
            }
            log.warn("[AUTH] Microservice returned a response but no token. Will fallback.");
        } catch (Exception e) {
            log.warn("[AUTH] ⚠️ Microservice login FAILED — switching to fallback. Reason: {}", e.getMessage());
        }
        return null;
    }

    // Handles the login form submission. If successful, we store the user in the session.
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password, 
                        HttpSession session, 
                        Model model) {
        try {
            User user = tryMicroserviceLogin(email, password);
            if (user == null) {
                // Fallback to local DB authentication if microservice fails / is unreachable
                log.info("[AUTH] 🔄 Using FALLBACK: authenticating '{}' directly via local database", email);
                user = userService.login(email, password);
                log.info("[AUTH] ✅ Fallback login SUCCEEDED for user: {} | Role: {}", user.getEmail(), user.getRole());
            }
            session.setAttribute("user", user); // This keeps the user logged in as they navigate
            return "redirect:/dashboard";
        } catch (Exception e) {
            log.error("[AUTH] ❌ Login FAILED for user: {} | Reason: {}", email, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "index"; // Go back to login if it failed
        }
    }

    // Handles new user registration and saves them to the database
    @PostMapping("/register")
    public String register(@RequestParam String email, 
                           @RequestParam String password, 
                           @RequestParam String role, 
                           Model model) {
        try {
            userService.register(email, password, Role.valueOf(role.toUpperCase()));
            model.addAttribute("success", "Registration successful! Please sign in.");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // Clears the session and sends the user back to the homepage
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        try {
            session.invalidate();
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error logging out: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Show the reset password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        try {
            return "forgot_password";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading forgot password page: " + e.getMessage());
            return "forgot_password";
        }
    }

    // Processes the password reset request
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, 
                                       @RequestParam String password, 
                                       @RequestParam String confirmPassword, 
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "forgot_password";
        }

        try {
            // First check if email exists
            userService.findByEmail(email);
            // Then reset the password in the database
            userService.resetPassword(email, password);
            model.addAttribute("success", "Password reset successful. Please login.");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Email not found in our system.");
            return "forgot_password";
        }
    }
}
