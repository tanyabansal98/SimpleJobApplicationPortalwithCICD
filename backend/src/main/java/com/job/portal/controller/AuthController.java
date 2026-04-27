package com.job.portal.controller;


import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import com.job.portal.service.interfaces.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {


    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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

    // Handles the login form submission. If successful, we store the user in the session.
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password, 
                        HttpSession session, 
                        Model model) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("user", user); // This keeps the user logged in as they navigate
            return "redirect:/dashboard";
        } catch (Exception e) {
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
