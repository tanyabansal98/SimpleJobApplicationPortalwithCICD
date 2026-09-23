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
        if (path == null)
            path = "";

        // 1. Allow public routes
        if (path.isEmpty() || path.equals("/") || path.equals("/login") || path.equals("/register")
                || path.equals("/forgot-password")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.endsWith(".jsp")
                || request.getQueryString() != null && request.getQueryString().contains("error")) {
            return true;
        }

        // 2. Check if JWT already authenticated this request
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return true; // JWT is valid, let the request through
        }

        // 3. Check session (existing browser login)
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            if (path.isEmpty() || path.equals("/")) {
                return true;
            }
            response.sendRedirect(request.getContextPath() + "/?error=Please login first.");
            return false;
        }

        // 4. Role-based access
        if (path.startsWith("/admin/") && user.getRole() != Role.ADMIN) {
            response.sendRedirect("/dashboard?error=Unauthorized access.");
            return false;
        }
        if (path.startsWith("/student/") && user.getRole() != Role.STUDENT) {
            response.sendRedirect("/dashboard?error=Unauthorized access.");
            return false;
        }
        if (path.startsWith("/employer/") && user.getRole() != Role.EMPLOYER) {
            response.sendRedirect("/dashboard?error=Unauthorized access.");
            return false;
        }

        return true;
    }
}