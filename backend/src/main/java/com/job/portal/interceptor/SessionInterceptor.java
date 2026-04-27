package com.job.portal.interceptor;

import com.job.portal.model.User;
import com.job.portal.model.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // SECURITY NOTE: InputValidator.isClean() can be called here to validate
        // all request parameters before they reach any controller.
        // Example:
        // java.util.Enumeration<String> params = request.getParameterNames();
        // while (params.hasMoreElements()) {
        // String value = request.getParameter(params.nextElement());
        // if (value != null && !InputValidator.isClean(value)) {
        // response.sendRedirect("/login?error=Invalid+input+detected");
        // return false;
        // }
        // }

        String path = request.getServletPath();
        if (path == null) path = "";

        // 1. Allow public routes
        if (path.isEmpty() || path.equals("/") || path.equals("/login") || path.equals("/register") || path.equals("/forgot-password")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.endsWith(".jsp") || request.getQueryString() != null && request.getQueryString().contains("error")) {
            return true;
        }

        // 2. Check session
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Prevent infinite loop: if we are already on the root with an error, don't redirect again
            if (path.isEmpty() || path.equals("/")) {
                return true; 
            }
            response.sendRedirect(request.getContextPath() + "/?error=Please login first.");
            return false;
        }

        // 3. Role-based access
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
