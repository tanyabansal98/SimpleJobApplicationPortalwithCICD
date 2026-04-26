<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Job Portal - Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        function setRole(role) {
            document.getElementById('roleInput').value = role;
            document.querySelectorAll('.role-option').forEach(opt => opt.classList.remove('active'));
            document.getElementById('role-' + role.toLowerCase()).classList.add('active');
        }
    </script>
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
                <h1>Create Account</h1>
                <p class="muted">Join our community today</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    ${error}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
                <input type="hidden" name="role" id="roleInput" value="STUDENT">

                <div class="form-group">
                    <label class="label">I am a...</label>
                    <div class="role-selector">
                        <div id="role-student" class="role-option active" onclick="setRole('STUDENT')">Student</div>
                        <div id="role-employer" class="role-option" onclick="setRole('EMPLOYER')">Employer</div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="label">Email Address</label>
                    <input type="email" name="email" class="input" placeholder="name@email.com" required>
                </div>
                <div class="form-group">
                    <label class="label">Password</label>
                    <input type="password" name="password" class="input" placeholder="Min. 6 characters" required minlength="6">
                </div>
                
                <button type="submit" class="btn btn-primary btn-block mt-4">Create Account</button>
            </form>

            <div class="mt-8 text-center muted">
                Already have an account? 
                <a href="${pageContext.request.contextPath}/" style="color: var(--primary); font-weight: 600;">Sign In</a>
            </div>
        </div>
    </div>
</body>
</html>
