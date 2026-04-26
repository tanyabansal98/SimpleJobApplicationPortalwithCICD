<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Employer Dashboard"/>
</jsp:include>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <div>
            <h1>Employer Dashboard</h1>
            <p class="muted">Manage your job postings and review applications</p>
        </div>
        <a href="${pageContext.request.contextPath}/employer/jobs/new" class="btn btn-primary">+ Post New Job</a>
    </div>

    <div style="display: grid; grid-template-columns: 1fr; gap: 1.5rem;">
        <c:forEach var="job" items="${jobs}">
            <c:if test="${not empty job.jobId and not empty job.title}">
                <div style="padding: 1.5rem; border: 1px solid var(--border); border-radius: 8px; background: white;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                        <div>
                            <h3 style="margin-bottom: 0.5rem; color: var(--primary); font-size: 1.25rem;">${job.title}</h3>
                            <div style="display: flex; gap: 1rem; font-size: 0.85rem;" class="muted">
                                <span><strong>Location:</strong> ${job.location}</span>
                                <span>|</span>
                                <span><strong>Created:</strong> ${job.formattedCreatedAt}</span>
                                <span>|</span>
                                <span><strong>Status:</strong> 
                                    <span style="color: ${job.active ? '#10b981' : '#f43f5e'}; font-weight: 600;">
                                        ${job.active ? 'Active' : 'Archived'}
                                    </span>
                                </span>
                            </div>
                        </div>
                        <div style="display: flex; gap: 0.5rem;">
                            <a href="${pageContext.request.contextPath}/employer/applications/${job.jobId}" class="btn btn-primary" style="padding: 5px 15px; font-size: 0.875rem;">
                                View Applications
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
        
        <c:if test="${empty jobs}">
            <div style="padding: 3rem; text-align: center; border: 2px dashed var(--border); border-radius: 12px; background: #f8fafc;" class="muted">
                You haven't posted any jobs yet. Start by creating your first posting!
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
