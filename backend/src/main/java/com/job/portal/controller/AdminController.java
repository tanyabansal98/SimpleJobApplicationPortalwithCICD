package com.job.portal.controller;

import com.job.portal.service.interfaces.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Fetches high-level system stats for the Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("Inside dashboard method in AdminController");
        try {
            model.addAttribute("stats", adminService.getDashboardStats());
            log.info("Loading admin stats");
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin stats: " + e.getMessage());
            log.error("Unable to load admin stats due to error: {}", e.getMessage());
            return "admin/dashboard";
        }
    }

    // Displays a complete list of all registered users in the system
    @GetMapping("/users")
    public String listUsers(Model model) {
        log.info("Inside listUsers method in AdminController");
        try {
            model.addAttribute("users", adminService.listAllUsers());
            log.info("Displaying the list of application users: {}", adminService.listAllUsers());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            log.error("Unable to load the list of users due to error: {}", e.getMessage());
            return "admin/dashboard";
        }
    }

    // Allows the Admin to disable a user's account, preventing them from logging in
    @PostMapping("/users/deactivate")
    public String deactivateUser(@RequestParam Long userId, Model model) {
        log.info("Inside deactivateUser method in AdminController");
        try {
            adminService.deactivateUser(userId);
            log.info("Successfully deactivated the userId: {}", userId);
            return "redirect:/admin/users?success=User deactivated.";
        } catch (Exception e) {
            log.error("Unable to deactivate userid: {}", userId, e.getMessage());
            return "redirect:/admin/users?error=Could not deactivate user: " + e.getMessage();
        }
    }

    // Re-enables a previously deactivated user account
    @PostMapping("/users/activate")
    public String activateUser(@RequestParam Long userId, Model model) {
        log.info("Inside activateUser method in AdminController");
        try {
            adminService.activateUser(userId);
            log.info("Successfully activated the userId: {}", userId);
            return "redirect:/admin/users?success=User activated.";
        } catch (Exception e) {
            log.error("Unable to activate userid: {}", userId, e.getMessage());
            return "redirect:/admin/users?error=Could not activate user: " + e.getMessage();
        }
    }
}
