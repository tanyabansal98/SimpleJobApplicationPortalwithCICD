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
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    // Displays a complete list of all registered users in the system
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", adminService.listAllUsers());
        return "admin/users";
    }

    // Allows the Admin to disable a user's account, preventing them from logging in
    @PostMapping("/users/deactivate")
    public String deactivateUser(@RequestParam Long userId) {
        adminService.deactivateUser(userId);
        return "redirect:/admin/users?success=User deactivated.";
    }

    // Re-enables a previously deactivated user account
    @PostMapping("/users/activate")
    public String activateUser(@RequestParam Long userId) {
        adminService.activateUser(userId);
        return "redirect:/admin/users?success=User activated.";
    }
}
