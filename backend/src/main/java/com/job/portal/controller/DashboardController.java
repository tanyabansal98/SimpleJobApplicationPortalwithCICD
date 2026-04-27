package com.job.portal.controller;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Acts as a traffic controller right after login.

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");

            // No session? Send them back to login — nothing to show here.
            if (user == null) {
                return "redirect:/?error=Please login first.";
            }

            // Route each role to their dedicated dashboard.
            if (user.getRole() == Role.ADMIN) {
                return "redirect:/admin/dashboard";
            } else if (user.getRole() == Role.EMPLOYER) {
                return "redirect:/employer/dashboard";
            } else if (user.getRole() == Role.STUDENT) {
                return "redirect:/student/dashboard";
            }

            // Shouldn't happen in normal flow, but guard against corrupted role data.
            return "redirect:/?error=Invalid role.";
        } catch (Exception e) {
            return "redirect:/?error=Dashboard error: " + e.getMessage();
        }
    }
}
