<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Applications for ${job.title}"/>
</jsp:include>

<div style="margin-bottom: 2rem;">
    <a href="${pageContext.request.contextPath}/employer/dashboard" class="btn" style="text-decoration: none; display: inline-flex; align-items: center; background: #f1f5f9; color: #475569; padding: 8px 20px; border-radius: 6px; font-weight: 500; border: 1px solid #cbd5e1;">
        Back to Dashboard
    </a>
</div>

<div class="card">
    <div style="margin-bottom: 2.5rem;">
        <h1>Applications: ${job.title}</h1>
        <p class="muted">Review and manage candidates for this position</p>
    </div>

    <c:choose>
        <c:when test="${not empty applications}">
            <table style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr style="border-bottom: 2px solid var(--border); text-align: left;">
                        <th style="padding: 1rem 0;">Applicant Email</th>
                        <th style="padding: 1rem 0;">Resume</th>
                        <th style="padding: 1rem 0;">Current Status</th>
                        <th style="padding: 1rem 0;">Applied Date</th>
                        <th style="padding: 1rem 0;">Change Status</th>
                        <th style="padding: 1rem 0; text-align: right;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Loop through each application and show the candidate's info -->
                    <c:forEach var="app" items="${applications}">
                        <c:if test="${not empty app.applicationId and not empty app.student}">
                            <tr style="border-bottom: 1px solid var(--border);">
                                <td style="padding: 1rem 0; font-weight: 500;">${app.student.email}</td>
                                <td style="padding: 1rem 0;">
                                    <!-- Only show the 'View' link if the student has actually uploaded a resume -->
                                    <c:choose>
                                        <c:when test="${not empty app.student.studentProfile.resumeFileName}">
                                            <a href="${pageContext.request.contextPath}/api/applications/resume/${app.student.userId}" 
                                               target="_blank" 
                                               style="color: var(--primary); font-weight: 500; text-decoration: none;">
                                                View
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="muted" style="font-size: 0.875rem;">No Resume</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="padding: 1rem 0;">
                                    <!-- Current status badge (color updated via JavaScript on save) -->
                                    <span id="status-${app.applicationId}" style="padding: 4px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; 
                                        background: #f1f5f9; color: #475569;">
                                        ${app.status}
                                    </span>
                                </td>
                                <td style="padding: 1rem 0;" class="muted">${app.formattedAppliedAt}</td>
                                <td style="padding: 1rem 0;">
                                    <!-- Dropdown for the employer to select a new status -->
                                    <select id="select-${app.applicationId}" 
                                            style="padding: 6px; border-radius: 6px; border: 1px solid var(--border); font-size: 0.875rem;">
                                        <option value="">Select Status...</option>
                                        <option value="SUBMITTED" ${app.status == 'SUBMITTED' ? 'selected' : ''}>Submitted</option>
                                        <option value="UNDER_REVIEW" ${app.status == 'UNDER_REVIEW' ? 'selected' : ''}>Under Review</option>
                                        <option value="SHORTLISTED" ${app.status == 'SHORTLISTED' ? 'selected' : ''}>Shortlisted</option>
                                        <option value="HIRED" ${app.status == 'HIRED' ? 'selected' : ''}>Hired</option>
                                        <option value="REJECTED" ${app.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                                    </select>
                                </td>
                                <td style="padding: 1rem 0; text-align: right;">
                                    <!-- This button triggers an AJAX call to update the database without refreshing the page -->
                                    <button onclick="updateStatus(${app.applicationId})" 
                                            class="btn btn-primary" 
                                            style="padding: 5px 15px; font-size: 0.875rem;">
                                        Update
                                    </button>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div style="padding: 4rem 2rem; text-align: center; background: #f8fafc; border-radius: 12px; border: 1px dashed #cbd5e1;">
                <p class="muted" style="font-size: 1.1rem;">No applications received yet for this job.</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- In-page Notification -->
<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 1000; transition: all 0.3s ease; box-shadow: 0 4px 12px rgba(0,0,0,0.15);"></div>

<script>
function showNotification(msg, isError) {
    const notification = document.getElementById('notification');
    notification.innerText = msg;
    notification.style.background = isError ? '#ef4444' : '#10b981';
    notification.style.display = 'block';
    setTimeout(() => { notification.style.display = 'none'; }, 3000);
}

async function updateStatus(id) {
    const select = document.getElementById('select-' + id);
    const newStatus = select.value;
    
    if (!newStatus) {
        showNotification('Please select a status first.', true);
        return;
    }
    
    try {
        const response = await fetch('${pageContext.request.contextPath}/api/applications/status', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ 
                applicationId: id,
                status: newStatus 
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            const statusBadge = document.getElementById('status-' + id);
            statusBadge.innerText = newStatus;
            
            // Update colors based on status
            if (newStatus === 'HIRED') {
                statusBadge.style.background = '#dcfce7';
                statusBadge.style.color = '#166534';
            } else if (newStatus === 'REJECTED') {
                statusBadge.style.background = '#fee2e2';
                statusBadge.style.color = '#991b1b';
            } else {
                statusBadge.style.background = '#f1f5f9';
                statusBadge.style.color = '#475569';
            }
            
            showNotification('Status updated successfully!', false);
        } else {
            showNotification(data.error || 'Failed to update status', true);
        }
    } catch (err) {
        showNotification('An unexpected error occurred.', true);
    }
}
</script>

<jsp:include page="../common/footer.jsp"/>
