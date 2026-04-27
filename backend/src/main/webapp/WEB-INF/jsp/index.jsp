<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Job Portal - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/" class="logo">JobApplicationPortal</a>
        </div>
    </header>

    <div class="container">
        <div class="card auth-card">
            <div class="text-center mb-8">
                <h1>Welcome Back</h1>
                <p class="muted">Sign in to continue your journey</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    ${error}
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    ${success}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label class="label">Email Address</label>
                    <input type="email" name="email" class="input" placeholder="name@company.com" required>
                </div>
                <div class="form-group">
                    <label class="label">Password</label>
                    <input type="password" name="password" class="input" placeholder="••••••••" required>
                    <div style="text-align: right; margin-top: 5px;">
                        <a href="${pageContext.request.contextPath}/forgot-password" class="muted" style="font-size: 0.85rem;">Forgot Password?</a>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-block mt-4">Sign In</button>
            </form>

            <div class="mt-8 text-center muted">
                Don't have an account? 
                <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 600;">Create account</a>
            </div>
        </div>
    </div>
</body>
</html>
