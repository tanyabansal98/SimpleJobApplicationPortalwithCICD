<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Employer - Manage Jobs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        .job-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.5rem;
            margin-bottom: 1rem;
        }
        .job-info h3 {
            margin-bottom: 0.25rem;
        }
        .job-stats {
            display: flex;
            gap: 1.5rem;
            font-size: 0.875rem;
            color: var(--text-muted);
        }
        .actions {
            display: flex;
            gap: 0.75rem;
        }
        .badge-archived {
            background: #fee2e2;
            color: #b91c1c;
            font-size: 0.625rem;
            text-transform: uppercase;
            padding: 0.125rem 0.375rem;
            border-radius: 4px;
            font-weight: 700;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/dashboard" class="logo">JobApplicationPortal</a>
            <nav>
                <span style="margin-right: 1rem; opacity: 0.8;">Employer: ${sessionScope.user.email}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="color: #333; padding: 0.5rem 1rem;">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container" style="padding-top: 3rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
            <div>
                <h1>Manage Jobs</h1>
                <p class="muted">Scale your engineering team with ease</p>
            </div>
            <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary">Post a New Role</a>
        </div>

        <div class="job-list">
            <c:forEach var="job" items="${jobs}">
                <div class="card job-item ${!job.isActive ? 'archived' : ''}" style="${!job.isActive ? 'opacity: 0.6;' : ''}">
                    <div class="job-info">
                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                            <h3>${job.title}</h3>
                            <c:if test="${!job.isActive}">
                                <span class="badge-archived">Archived</span>
                            </c:if>
                        </div>
                        <div class="job-stats">
                            <span>${job.location}</span>
                            <span>Full-time</span> <!-- Simplified for now -->
                        </div>
                    </div>
                    <div class="actions">
                        <a href="${pageContext.request.contextPath}/employer/candidates?jobId=${job.jobId}" class="btn btn-secondary">View Applicants</a>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty jobs}">
                <div class="card text-center" style="padding: 4rem; border-style: dashed;">
                    <h3 style="color: var(--text-muted);">No jobs posted yet.</h3>
                    <p class="muted">Start by posting your first job opportunity!</p>
                    <a href="${pageContext.request.contextPath}/employer/post-job" class="btn btn-primary mt-4">Post a Job</a>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
