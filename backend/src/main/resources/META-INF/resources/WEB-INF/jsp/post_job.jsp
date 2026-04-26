<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Employer - Post Job</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/dashboard" class="logo">JobApplicationPortal</a>
        </div>
    </header>

    <div class="container" style="padding-top: 3rem;">
        <div class="card" style="max-width: 600px; margin: 0 auto;">
            <h2 style="margin-bottom: 1.5rem;">Post a New Role</h2>
            
            <form action="${pageContext.request.contextPath}/employer/post-job" method="post">
                <div class="form-group">
                    <label class="label">Job Title</label>
                    <input type="text" name="title" class="input" placeholder="e.g. Senior Software Engineer" required>
                </div>
                
                <div class="form-group">
                    <label class="label">Location</label>
                    <input type="text" name="location" class="input" placeholder="e.g. San Francisco, CA" required>
                </div>

                <div class="form-group">
                    <label class="label">Salary (Annual)</label>
                    <input type="number" name="salary" class="input" placeholder="e.g. 120000" required>
                </div>

                <div class="form-group">
                    <label class="label">Description</label>
                    <textarea name="description" class="input" style="min-height: 150px;" placeholder="Tell us about the role..." required></textarea>
                </div>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <button type="submit" class="btn btn-primary" style="flex: 1;">Publish Job</button>
                    <a href="${pageContext.request.contextPath}/employer/manage-jobs" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
