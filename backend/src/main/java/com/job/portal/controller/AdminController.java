package com.job.portal.controller;

import com.job.portal.service.interfaces.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Fetches high-level system stats for the Admin Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            model.addAttribute("stats", adminService.getDashboardStats());
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading admin stats: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    // Displays a complete list of all registered users in the system
    @GetMapping("/users")
    public String listUsers(Model model) {
        try {
            model.addAttribute("users", adminService.listAllUsers());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    // Allows the Admin to disable a user's account, preventing them from logging in
    @PostMapping("/users/deactivate")
    public String deactivateUser(@RequestParam Long userId, Model model) {
        try {
            adminService.deactivateUser(userId);
            return "redirect:/admin/users?success=User deactivated.";
        } catch (Exception e) {
            return "redirect:/admin/users?error=Could not deactivate user: " + e.getMessage();
        }
    }

    // Re-enables a previously deactivated user account
    @PostMapping("/users/activate")
    public String activateUser(@RequestParam Long userId, Model model) {
        try {
            adminService.activateUser(userId);
            return "redirect:/admin/users?success=User activated.";
        } catch (Exception e) {
            return "redirect:/admin/users?error=Could not activate user: " + e.getMessage();
        }
    }
}
