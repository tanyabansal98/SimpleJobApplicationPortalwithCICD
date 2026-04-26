<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard"/>
</jsp:include>

<div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; margin-bottom: 2rem;">
    <div class="card" style="padding: 1.5rem; text-align: center;">
        <h3 class="muted" style="font-size: 0.875rem; text-transform: uppercase;">Total Users</h3>
        <p style="font-size: 2rem; font-weight: 700; color: var(--primary);">${stats.totalUsers}</p>
    </div>
    <div class="card" style="padding: 1.5rem; text-align: center;">
        <h3 class="muted" style="font-size: 0.875rem; text-transform: uppercase;">Total Jobs</h3>
        <p style="font-size: 2rem; font-weight: 700; color: var(--success);">${stats.totalJobs}</p>
    </div>
    <div class="card" style="padding: 1.5rem; text-align: center;">
        <h3 class="muted" style="font-size: 0.875rem; text-transform: uppercase;">Active Jobs</h3>
        <p style="font-size: 2rem; font-weight: 700; color: var(--primary);">${stats.activeJobs}</p>
    </div>
    <div class="card" style="padding: 1.5rem; text-align: center;">
        <h3 class="muted" style="font-size: 0.875rem; text-transform: uppercase;">Applications</h3>
        <p style="font-size: 2rem; font-weight: 700; color: #f59e0b;">${stats.totalApplications}</p>
    </div>
</div>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h1>Admin Overview</h1>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
    </div>
    
    <div style="padding: 2rem; background: #f8fafc; border-radius: 8px; text-align: center;">
        <p class="muted">Detailed system reports and logs will be available here in the final release.</p>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
