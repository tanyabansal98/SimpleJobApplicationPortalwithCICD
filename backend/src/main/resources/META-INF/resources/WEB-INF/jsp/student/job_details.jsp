<!-- JSP VERSION 1.1 - FORCE RECOMPILE -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="${job.title}"/>
</jsp:include>

<div style="max-width: 900px; margin: 0 auto;">
    <div style="margin-bottom: 2rem;">
        <a href="${pageContext.request.contextPath}/student/jobs" class="btn" style="text-decoration: none; display: inline-flex; align-items: center; background: #f1f5f9; color: #475569; padding: 8px 20px; border-radius: 6px; font-weight: 500; border: 1px solid #cbd5e1;">
            Back to Job Board
        </a>
    </div>

    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem;">
            <div>
                <h1 style="margin-bottom: 0.5rem;">${job.title}</h1>
                <p style="font-size: 1.125rem; font-weight: 500; color: var(--primary); margin-bottom: 0.5rem;">${job.displayCompanyName}</p>
                <div style="display: flex; gap: 1.5rem;" class="muted">
                    <span>Location: ${job.location}</span>
                    <span>|</span>
                    <span>Posted: ${job.formattedCreatedAt}</span>
                </div>
            </div>
            <c:choose>
                <c:when test="${alreadyApplied}">
                    <button class="btn" disabled style="padding: 12px 30px; font-size: 1.125rem; background: #e2e8f0; color: #64748b; border: 1px solid #cbd5e1; cursor: not-allowed;">
                        Applied ✓
                    </button>
                </c:when>
                <c:otherwise>
                    <button id="applyBtn" class="btn btn-primary" onclick="applyToJob(${job.jobId})" 
                        style="padding: 12px 30px; font-size: 1.125rem;">
                        Apply Now
                    </button>
                </c:otherwise>
            </c:choose>
        </div>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 3rem;">
            <div>
                <section class="mb-8">
                    <h2 style="margin-bottom: 1rem; font-size: 1.25rem;">Job Description</h2>
                    <p style="white-space: pre-line; color: #4b5563;">${job.description}</p>
                </section>

                <section class="mb-8">
                    <h2 style="margin-bottom: 1rem; font-size: 1.25rem;">Required Skills</h2>
                    <p class="muted">${not empty job.requiredSkills ? job.requiredSkills : 'Not specified'}</p>
                </section>
            </div>

            <div>
                <div style="background: #f8fafc; padding: 1.5rem; border-radius: 8px; border: 1px solid var(--border);">
                    <h3 style="font-size: 1rem; margin-bottom: 1rem;">Quick Summary</h3>
                    <div style="display: grid; gap: 1rem; font-size: 0.875rem;">
                        <div>
                            <span class="label" style="margin-bottom: 0.25rem;">Salary Range</span>
                            <span style="font-weight: 600;">${not empty job.salaryRange ? job.salaryRange : 'Not disclosed'}</span>
                        </div>
                        <div>
                            <span class="label" style="margin-bottom: 0.25rem;">Job Type</span>
                            <span style="font-weight: 600;">${job.jobType}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- In-page Notification -->
<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 1000; transition: all 0.3s ease;"></div>

<script>
async function applyToJob(jobId) {
    const btn = document.getElementById('applyBtn');
    const notification = document.getElementById('notification');
    const originalText = btn.innerText;
    
    function showNotification(msg, isError) {
        notification.innerText = msg;
        notification.style.background = isError ? '#ef4444' : '#10b981';
        notification.style.display = 'block';
        setTimeout(() => { notification.style.display = 'none'; }, 3000);
    }
    
    try {
        btn.disabled = true;
        btn.innerText = 'Submitting...';
        
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
            btn.innerText = 'Applied ✓';
            btn.onclick = null;
            btn.style.cursor = 'not-allowed';
            showNotification('Successfully applied to the job.', false);
        } else {
            btn.disabled = false;
            btn.innerText = originalText;
            showNotification(data.error || 'Failed to apply', true);
        }
    } catch (err) {
        btn.disabled = false;
        btn.innerText = originalText;
        showNotification('An unexpected error occurred.', true);
    }
}
</script>

<jsp:include page="../common/footer.jsp"/>
