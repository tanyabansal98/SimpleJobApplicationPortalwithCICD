<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student - Job Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        .job-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, min-min(300px, 1fr));
            gap: 1.5rem;
            margin-top: 2rem;
        }
        .job-card {
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .job-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 0.25rem;
        }
        .company-name {
            color: var(--primary);
            font-weight: 600;
            font-size: 0.875rem;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 1rem;
            display: block;
        }
        .job-desc {
            color: var(--text-muted);
            font-size: 0.875rem;
            margin-bottom: 1.5rem;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .job-meta {
            font-size: 0.875rem;
            color: #4b5563;
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .status-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 700;
            background: #dcfce7;
            color: #15803d;
            border: 1px solid #bbf7d0;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/dashboard" class="logo">JobApplicationPortal</a>
            <nav>
                <span style="margin-right: 1rem; opacity: 0.8;">Hello, ${sessionScope.user.email}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="color: #333; padding: 0.5rem 1rem;">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container" style="padding-top: 3rem; padding-bottom: 3rem;">
        <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 2rem;">
            <div>
                <h1>Find Your Dream Job</h1>
                <p class="muted">Curated opportunities for the next generation of talent</p>
            </div>
        </div>

        <div class="job-grid">
            <c:forEach var="job" items="${jobs}">
                <div class="card job-card">
                    <div>
                        <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                            <div style="width: 48px; height: 48px; background: #eef2ff; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: var(--primary); margin-bottom: 1rem;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="14" x="2" y="7" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></svg>
                            </div>
                        </div>
                        <span class="job-title">${job.title}</span>
                        <span class="company-name">${job.displayCompanyName}</span>
                        <p class="job-desc">${job.description}</p>
                        
                        <div class="job-meta">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
                            ${job.location}
                        </div>
                        <div class="job-meta" style="margin-bottom: 1.5rem;">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                            ${job.salary > 0 ? '$'.concat(job.salary).concat(' / year') : 'Salary Not Disclosed'}
                        </div>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/student/apply" method="post">
                        <input type="hidden" name="jobId" value="${job.jobId}">
                        <button type="submit" class="btn btn-primary btn-block">Quick Apply</button>
                    </form>
                </div>
            </c:forEach>
        </div>

        <c:if test="${empty jobs}">
            <div class="card text-center" style="padding: 4rem; border-style: dashed;">
                <h3 style="color: var(--text-muted);">No jobs available at the moment.</h3>
                <p class="muted">Check back later for new opportunities!</p>
            </div>
        </c:if>
    </div>
</body>
</html>
