<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Forgot Password - Job Portal</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .forgot-card { max-width: 400px; margin: 100px auto; border-radius: 15px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .btn-primary { background-color: #3f51b5; border: none; }
        .btn-primary:hover { background-color: #303f9f; }
    </style>
</head>
<body>
    <div class="card forgot-card">
        <div class="card-body p-5">
            <h3 class="text-center mb-4">Reset Password</h3>
            <p class="text-muted text-center mb-4">Enter your email address and we'll help you reset your password.</p>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="/forgot-password" method="POST">
                <div class="mb-3">
                    <label class="form-label">Email Address</label>
                    <input type="email" name="email" class="form-control" placeholder="name@company.com" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">New Password</label>
                    <input type="password" name="password" class="form-control" placeholder="••••••••" required minlength="6">
                </div>
                <div class="mb-4">
                    <label class="form-label">Confirm Password</label>
                    <input type="password" name="confirmPassword" class="form-control" placeholder="••••••••" required minlength="6">
                </div>
                <button type="submit" class="btn btn-primary w-100 py-2">Update Password</button>
            </form>
            
            <div class="text-center mt-4">
                <a href="/login" class="text-decoration-none">Back to Login</a>
            </div>
        </div>
    </div>
</body>
</html>
