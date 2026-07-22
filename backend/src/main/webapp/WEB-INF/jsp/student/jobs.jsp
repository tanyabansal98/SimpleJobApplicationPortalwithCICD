<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Job Board"/>
</jsp:include>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <div>
            <h1>Job Board</h1>
            <p class="muted">Find your next opportunity</p>
        </div>
        <div style="display: flex; gap: 1rem; align-items: center;">
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn" style="background: #f8fafc; color: #475569; border: 1px solid var(--border);">Back to Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/jobs/matches" class="btn btn-primary">Jobs For You</a>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-primary">My Applications</a>
        </div>
    </div>

    <!-- Resume required warning banner -->
    <c:if test="${not hasResume}">
        <div style="background: #fffbeb; border: 1px solid #f59e0b; border-radius: 8px; padding: 1rem 1.5rem; margin-bottom: 1.5rem; display: flex; align-items: center; justify-content: space-between; gap: 1rem;">
            <div style="display: flex; align-items: center; gap: 0.75rem;">
                <span style="font-size: 1.25rem;">&#9888;</span>
                <span style="color: #92400e; font-weight: 500;">You need to upload a resume before you can apply to any job.</span>
            </div>
            <a href="${pageContext.request.contextPath}/student/profile" 
               style="background: #f59e0b; color: white; padding: 8px 18px; border-radius: 6px; font-weight: 600; text-decoration: none; white-space: nowrap; font-size: 0.875rem;">
                Upload Resume
            </a>
        </div>
    </c:if>

    <!-- Search Filters -->
    <form action="${pageContext.request.contextPath}/student/jobs" method="get" 
          style="display: grid; grid-template-columns: 1fr 1fr auto; gap: 1rem; margin-bottom: 2rem; padding: 1.5rem; background: #f8fafc; border-radius: 8px; border: 1px solid var(--border);">
        <div class="form-group" style="margin-bottom: 0;">
            <input type="text" name="title" class="input" placeholder="Search by title..." value="${param.title}">
        </div>
        <div class="form-group" style="margin-bottom: 0;">
            <input type="text" name="location" class="input" placeholder="Location..." value="${param.location}">
        </div>
        <button type="submit" class="btn btn-primary">Filter</button>
    </form>

    <!-- Job Grid -->
    <div style="display: grid; grid-template-columns: 1fr; gap: 1rem;">
        <c:forEach var="job" items="${jobs}">
            <c:if test="${not empty job.jobId and not empty job.title}">
                <div style="padding: 1.5rem; border: 1px solid var(--border); border-radius: 8px; display: flex; justify-content: space-between; align-items: center; transition: transform 0.2s; cursor: pointer;"
                     onmouseover="this.style.borderColor='var(--primary)'" 
                     onmouseout="this.style.borderColor='var(--border)'"
                     onclick="window.location.href='${pageContext.request.contextPath}/student/jobs/${job.jobId}'">
                    <div>
                        <h3 style="margin-bottom: 0.25rem; color: var(--primary); font-size: 1.15rem;">${job.title}</h3>
                        <p style="font-weight: 600; margin-bottom: 0.5rem; font-size: 0.95rem;">${job.displayCompanyName}</p>
                        <div style="display: flex; gap: 1rem; font-size: 0.85rem; align-items: center;" class="muted">
                            <span><strong style="color: #64748b;">Loc:</strong> ${job.location}</span>
                            <span>|</span>
                            <span><strong style="color: #64748b;">Salary:</strong> ${not empty job.salaryRange ? job.salaryRange : 'Not disclosed'}</span>
                        </div>
                    </div>
                    <div style="display: flex; gap: 0.5rem;">
                        <c:set var="isApplied" value="false" />
                        <c:forEach var="appliedId" items="${appliedJobIds}">
                            <c:if test="${appliedId == job.jobId}">
                                <c:set var="isApplied" value="true" />
                            </c:if>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${isApplied}">
                                <button class="btn" disabled style="background: #e2e8f0; color: #64748b; border: 1px solid #cbd5e1; cursor: not-allowed;">Applied</button>
                            </c:when>
                            <c:when test="${not hasResume}">
                                <!-- No resume — show a disabled button that links to profile -->
                                <a href="${pageContext.request.contextPath}/student/profile"
                                   onclick="event.stopPropagation()"
                                   title="Upload a resume on your profile page first"
                                   style="display: inline-block; padding: 8px 16px; border-radius: 6px; font-size: 0.875rem; font-weight: 500;
                                          background: #fef3c7; color: #92400e; border: 1px solid #f59e0b; text-decoration: none; cursor: pointer;">
                                    Upload Resume First
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button id="apply-btn-${job.jobId}" class="btn btn-primary" 
                                        onclick="event.stopPropagation(); quickApply(${job.jobId})" 
                                        style="background: #10b981; border: 1px solid #10b981;">Apply</button>
                            </c:otherwise>
                        </c:choose>
                        <button class="btn btn-primary" style="background: transparent; border: 1px solid var(--primary); color: var(--primary);">View Details</button>
                    </div>
                </div>
            </c:if>
        </c:forEach>
        
        <c:choose>
            <c:when test="${empty jobs}">
                <div style="padding: 3rem; text-align: center;" class="muted">
                    No jobs found matching your criteria.
                </div>
            </c:when>
        </c:choose>
    </div>
</div>

<!-- In-page Notification -->
<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 1000; transition: all 0.3s ease;"></div>

<script>
async function quickApply(jobId) {
    const btn = document.getElementById('apply-btn-' + jobId);
    const notification = document.getElementById('notification');
    
    function showNotification(msg, isError) {
        notification.innerText = msg;
        notification.style.background = isError ? '#ef4444' : '#10b981';
        notification.style.display = 'block';
        setTimeout(() => { notification.style.display = 'none'; }, 3000);
    }
    
    try {
        btn.disabled = true;
        btn.innerText = '...';
        
        const response = await fetch('${pageContext.request.contextPath}/api/applications/apply', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ jobId: jobId })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            btn.style.background = '#e2e8f0';
            btn.style.color = '#64748b';
            btn.style.borderColor = '#cbd5e1';
            btn.innerText = 'Applied';
            btn.onclick = null;
            btn.style.cursor = 'not-allowed';
            showNotification('Successfully applied to the job.', false);
        } else {
            btn.disabled = false;
            btn.innerText = 'Apply';
            showNotification(data.error || 'Failed to apply', true);
        }
    } catch (err) {
        btn.disabled = false;
        btn.innerText = 'Apply';
        showNotification('An unexpected error occurred.', true);
    }
}
</script>

<jsp:include page="../common/footer.jsp"/>
