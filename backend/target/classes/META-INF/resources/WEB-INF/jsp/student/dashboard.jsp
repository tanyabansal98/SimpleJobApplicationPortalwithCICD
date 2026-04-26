<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Student Dashboard"/>
</jsp:include>

<div class="card">
    <!-- Top section with page title and quick-action buttons -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2.5rem;">
        <div>
            <h1>My Applications</h1>
            <p class="muted">Track the status of your current job applications</p>
        </div>
        <div style="display: flex; gap: 1rem;">
            <!-- Single button to manage resumes, as requested -->
            <a href="${pageContext.request.contextPath}/student/profile" class="btn btn-primary">Upload Resume</a>
            <a href="${pageContext.request.contextPath}/student/jobs" class="btn" style="background: white; border: 1px solid var(--border); color: var(--primary);">Browse Jobs</a>
        </div>
    </div>

    <!-- Check if the student has actually applied to any jobs yet -->
    <c:choose>
        <c:when test="${not empty applications}">
            <table style="width: 100%; border-collapse: collapse; margin-top: 1rem;">
                <thead>
                    <tr style="border-bottom: 2px solid var(--border); text-align: left;">
                        <th style="padding: 1rem 0;">Job Title</th>
                        <th style="padding: 1rem 0;">Company</th>
                        <th style="padding: 1rem 0;">Status</th>
                        <th style="padding: 1rem 0;">Applied On</th>
                        <th style="padding: 1rem 0;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Loop through each application and show its details -->
                    <c:forEach var="app" items="${applications}">
                        <c:if test="${not empty app.applicationId and not empty app.job}">
                            <tr id="row-${app.applicationId}" style="border-bottom: 1px solid var(--border);">
                                <td style="padding: 1rem 0; font-weight: 500;">${app.job.title}</td>
                                <td style="padding: 1rem 0;">${app.job.displayCompanyName}</td>
                                <td style="padding: 1rem 0;">
                                    <!-- Color-code the status based on whether they were hired or not -->
                                    <span id="status-${app.applicationId}" style="padding: 4px 12px; border-radius: 20px; font-size: 0.75rem; font-weight: 600; 
                                        background: ${app.status == 'HIRED' ? '#dcfce7' : '#f1f5f9'};
                                        color: ${app.status == 'HIRED' ? '#166534' : '#475569'};">
                                        ${app.status}
                                    </span>
                                </td>
                                <td style="padding: 1rem 0;" class="muted">${app.formattedAppliedAt}</td>
                                <td style="padding: 1rem 0;">
                                    <!-- Students can only withdraw an application if it's still in 'SUBMITTED' status -->
                                    <c:if test="${app.status == 'SUBMITTED'}">
                                        <button onclick="withdrawApplication(${app.applicationId})" 
                                                style="background: transparent; color: var(--danger); border: none; font-size: 0.875rem; cursor: pointer; text-decoration: underline;">
                                            Withdraw
                                        </button>
                                    </c:if>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <!-- Friendly message for new students who haven't applied anywhere yet -->
            <div style="padding: 4rem 2rem; text-align: center; background: #f8fafc; border-radius: 12px; border: 1px dashed #cbd5e1; margin-top: 1.5rem;">
                <p class="muted" style="font-size: 1.1rem; margin-bottom: 1rem;">You haven't applied to any jobs yet.</p>
                <a href="${pageContext.request.contextPath}/student/jobs" class="btn btn-primary">Start Browsing Jobs</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- In-page Notification -->
<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 1000; transition: all 0.3s ease; box-shadow: 0 4px 12px rgba(0,0,0,0.15);"></div>

<!-- Custom Confirmation Modal -->
<div id="confirmModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 2000; align-items: center; justify-content: center;">
    <div class="card" style="max-width: 400px; width: 90%; text-align: center; padding: 2rem;">
        <h3 style="margin-bottom: 1rem;">Withdraw Application?</h3>
        <p class="muted" style="margin-bottom: 2rem;">Are you sure you want to withdraw this application? You can apply again later if you change your mind.</p>
        <div style="display: flex; gap: 1rem;">
            <button onclick="closeModal()" class="btn" style="flex: 1; background: #f1f5f9; color: #475569;">Cancel</button>
            <button id="confirmWithdrawBtn" class="btn btn-primary" style="flex: 1; background: #ef4444; border-color: #ef4444;">Withdraw</button>
        </div>
    </div>
</div>

<script>
let applicationToWithdraw = null;

function closeModal() {
    document.getElementById('confirmModal').style.display = 'none';
}

function showNotification(msg, isError) {
    const notification = document.getElementById('notification');
    notification.innerText = msg;
    notification.style.background = isError ? '#ef4444' : '#10b981';
    notification.style.display = 'block';
    setTimeout(() => { notification.style.display = 'none'; }, 3000);
}

function withdrawApplication(id) {
    applicationToWithdraw = id;
    document.getElementById('confirmModal').style.display = 'flex';
}

document.getElementById('confirmWithdrawBtn').onclick = async function() {
    if (!applicationToWithdraw) return;
    closeModal();
    
    try {
        const response = await fetch('${pageContext.request.contextPath}/api/applications/withdraw/' + applicationToWithdraw, {
            method: 'POST'
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showNotification('Application withdrawn successfully.', false);
            setTimeout(() => { window.location.reload(); }, 1500);
        } else {
            showNotification(data.error || 'Failed to withdraw application', true);
        }
    } catch (err) {
        showNotification('An unexpected error occurred.', true);
    } finally {
        applicationToWithdraw = null;
    }
};
</script>

<jsp:include page="../common/footer.jsp"/>
