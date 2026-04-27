<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : 'Job Portal'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <!-- The header stays consistent across all pages of the portal -->
    <header>
        <div class="container">
            <!-- Clicking the logo always takes you back to your main dashboard -->
            <a href="${pageContext.request.contextPath}/dashboard" class="logo">JobApplicationPortal</a>
            
            <nav>
                <!-- If a user is logged in, show their email and a logout button -->
                <c:if test="${not empty sessionScope.user}">
                    <span style="margin-right: 20px; color: #cbd5e1;">Welcome, ${sessionScope.user.email}</span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-primary" style="background: var(--danger); padding: 5px 15px;">Logout</a>
                </c:if>
            </nav>
        </div>
    </header>
    <main class="container mt-8">
