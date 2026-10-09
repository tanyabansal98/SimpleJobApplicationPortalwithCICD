package com.job.portal.interceptor;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getServletPath();

        // 1. Public routes
        if (path == null || path.isEmpty() || path.equals("/") || path.equals("/login")
                || path.equals("/register") || path.equals("/forgot-password")) {
            return true;
        }

        // 2. JWT-authenticated request
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return true;
        }

        // 3. Session login
        HttpSession session = request.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/?error=Please login first.");
            return false;
        }

        // 4. Role check
        Role required = null;
        if (path.startsWith("/admin/"))
            required = Role.ADMIN;
        else if (path.startsWith("/student/"))
            required = Role.STUDENT;
        else if (path.startsWith("/employer/"))
            required = Role.EMPLOYER;

        if (required != null && user.getRole() != required) {
            response.sendRedirect("/dashboard?error=Unauthorized access.");
            return false;
        }

        return true;
    }
}