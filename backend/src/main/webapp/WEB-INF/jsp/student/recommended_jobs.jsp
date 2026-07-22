<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Recommended Jobs"/>
</jsp:include>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <div>
            <h1>Jobs Matched to Your Resume</h1>
            <p class="muted">Ranked by fit to your resume, powered by AI</p>
        </div>
        <div style="display: flex; gap: 1rem; align-items: center;">
            <a href="${pageContext.request.contextPath}/student/jobs" class="btn" style="background: #f8fafc; color: #475569; border: 1px solid var(--border);">Back to Job Board</a>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn" style="background: #f8fafc; color: #475569; border: 1px solid var(--border);">Back to Dashboard</a>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-primary">My Applications</a>
        </div>
    </div>

    <!-- Container for dynamic elements -->
    <div id="content-area">
        <c:choose>
            <c:when test="${not hasResume}">
                <!-- No resume — show upload prompt immediately and don't fetch -->
                <div style="background: #fffbeb; border: 1px solid #f59e0b; border-radius: 8px; padding: 2rem; text-align: center; margin-top: 1rem;">
                    <span style="font-size: 2rem; display: block; margin-bottom: 0.5rem;">&#9888;</span>
                    <h3 style="color: #92400e; margin-bottom: 0.5rem; font-size: 1.25rem;">Upload your resume to get personalized job recommendations</h3>
                    <p style="color: #b45309; margin-bottom: 1.5rem;">Our AI matchmaker scans your skills and experiences to rank the best opportunities for you.</p>
                    <a href="${pageContext.request.contextPath}/student/profile" 
                       style="display: inline-block; background: #f59e0b; color: white; padding: 10px 24px; border-radius: 6px; font-weight: 600; text-decoration: none;">
                        Upload Resume
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Loading State -->
                <div id="loading-state" style="padding: 3rem; text-align: center; color: #64748b;">
                    <p style="font-weight: 500; font-size: 1.1rem; margin-bottom: 0.5rem;">Loading your personalized recommendations...</p>
                    <p style="font-size: 0.9rem;" class="muted">AI is matching jobs with your profile</p>
                </div>

                <!-- Error State (hidden by default) -->
                <div id="error-state" style="display: none; background: #fef2f2; border: 1px solid #ef4444; border-radius: 8px; padding: 2rem; text-align: center; margin-top: 1rem;">
                    <span style="font-size: 2rem; display: block; margin-bottom: 0.5rem;">⚠️</span>
                    <h3 id="error-title" style="color: #b91c1c; margin-bottom: 0.5rem; font-size: 1.25rem;">Failed to load matches</h3>
                    <p id="error-message" style="color: #991b1b; margin-bottom: 1.5rem;">We couldn't retrieve your matches at this time.</p>
                    <div id="error-action-container"></div>
                </div>

                <!-- Job Grid (hidden by default) -->
                <div id="jobs-grid" style="display: none; grid-template-columns: 1fr; gap: 1rem;">
                    <!-- Job cards will be dynamically injected here -->
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- In-page Notification -->
<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 1000; transition: all 0.3s ease;"></div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // Only fetch matches if the student is flagged as having a resume
    if (${hasResume}) {
        fetchRecommendedJobs();
    }
});

async function fetchRecommendedJobs() {
    const loadingState = document.getElementById('loading-state');
    const errorState = document.getElementById('error-state');
    const jobsGrid = document.getElementById('jobs-grid');

    try {
        const response = await fetch('${pageContext.request.contextPath}/student/api/jobs/matches?topN=10');
        
        if (response.status === 400) {
            // Bad request typically means no resume uploaded (as per controller logic or fallback)
            showResumeUploadPrompt();
            return;
        }

        if (!response.ok) {
            throw new Error('API returned status: ' + response.status);
        }

        const matches = await response.json();
        loadingState.style.display = 'none';

        if (!matches || matches.length === 0) {
            jobsGrid.innerHTML = `
                <div style="padding: 3rem; text-align: center;" class="muted">
                    No strong matches found yet — check back as more jobs are posted.
                </div>
            `;
            jobsGrid.style.display = 'grid';
            return;
        }

        renderJobCards(matches);
    } catch (error) {
        console.error('Error fetching job matches:', error);
        loadingState.style.display = 'none';
        
        document.getElementById('error-message').innerText = 'An unexpected error occurred while looking up recommended jobs. Please try again later.';
        errorState.style.display = 'block';
    }
}

function showResumeUploadPrompt() {
    const loadingState = document.getElementById('loading-state');
    const errorState = document.getElementById('error-state');
    
    if (loadingState) loadingState.style.display = 'none';
    
    document.getElementById('error-title').innerText = 'Upload your resume to get personalized job recommendations';
    document.getElementById('error-message').innerText = 'Our AI matchmaker scans your skills and experiences to rank the best opportunities for you.';
    
    const actionContainer = document.getElementById('error-action-container');
    actionContainer.innerHTML = `
        <a href="${pageContext.request.contextPath}/student/profile" 
           style="display: inline-block; background: #f59e0b; color: white; padding: 10px 24px; border-radius: 6px; font-weight: 600; text-decoration: none;">
            Upload Resume
        </a>
    `;
    
    errorState.style.background = '#fffbeb';
    errorState.style.borderColor = '#f59e0b';
    errorState.querySelector('span').innerText = '⚠️';
    errorState.querySelector('h3').style.color = '#92400e';
    errorState.querySelector('p').style.color = '#b45309';
    errorState.style.display = 'block';
}

function renderJobCards(jobs) {
    const jobsGrid = document.getElementById('jobs-grid');
    jobsGrid.innerHTML = '';

    jobs.forEach((job, index) => {
        const card = document.createElement('div');
        card.style.padding = '1.5rem';
        card.style.border = '1px solid var(--border)';
        card.style.borderRadius = '8px';
        card.style.display = 'flex';
        card.style.justifyContent = 'space-between';
        card.style.alignItems = 'flex-start';
        card.style.transition = 'transform 0.2s';
        card.style.cursor = 'pointer';

        card.onmouseover = function() { this.style.borderColor = 'var(--primary)'; };
        card.onmouseout = function() { this.style.borderColor = 'var(--border)'; };
        card.onclick = function() { window.location.href = '${pageContext.request.contextPath}/student/jobs/' + job.jobId; };

        // Left Container (Title, Company, Loc, AI Reason)
        const leftContainer = document.createElement('div');
        leftContainer.style.flex = '1';
        leftContainer.style.marginRight = '1.5rem';

        // Title and Rank Badge
        const titleHeader = document.createElement('h3');
        titleHeader.style.marginBottom = '0.25rem';
        titleHeader.style.color = 'var(--primary)';
        titleHeader.style.fontSize = '1.15rem';
        titleHeader.style.display = 'flex';
        titleHeader.style.alignItems = 'center';
        titleHeader.style.gap = '0.5rem';

        const rankBadge = document.createElement('span');
        rankBadge.style.background = '#e0f2fe';
        rankBadge.style.color = '#0369a1';
        rankBadge.style.padding = '2px 8px';
        rankBadge.style.borderRadius = '12px';
        rankBadge.style.fontSize = '0.75rem';
        rankBadge.style.fontWeight = '700';
        rankBadge.innerText = `#${index + 1} Match`;

        titleHeader.appendChild(rankBadge);
        
        const titleText = document.createTextNode(' ' + job.title);
        titleHeader.appendChild(titleText);
        leftContainer.appendChild(titleHeader);

        // Company Name
        const companyPara = document.createElement('p');
        companyPara.style.fontWeight = '600';
        companyPara.style.marginBottom = '0.5rem';
        companyPara.style.fontSize = '0.95rem';
        companyPara.innerText = job.companyName || 'Unknown Company';
        leftContainer.appendChild(companyPara);

        // Location Info
        const infoDiv = document.createElement('div');
        infoDiv.style.display = 'flex';
        infoDiv.style.gap = '1rem';
        infoDiv.style.fontSize = '0.85rem';
        infoDiv.style.alignItems = 'center';
        infoDiv.className = 'muted';
        infoDiv.innerHTML = `<span><strong style="color: #64748b;">Loc:</strong> ${job.location || 'Not specified'}</span>`;
        leftContainer.appendChild(infoDiv);

        // AI Match Reason Callout
        if (job.reason) {
            const reasonCallout = document.createElement('div');
            reasonCallout.style.marginTop = '1rem';
            reasonCallout.style.padding = '0.75rem 1rem';
            reasonCallout.style.background = '#f0fdf4';
            reasonCallout.style.borderLeft = '4px solid #10b981';
            reasonCallout.style.borderRadius = '0 8px 8px 0';
            reasonCallout.style.fontSize = '0.875rem';
            reasonCallout.style.color = '#14532d';
            reasonCallout.style.display = 'flex';
            reasonCallout.style.alignItems = 'flex-start';
            reasonCallout.style.gap = '0.5rem';

            reasonCallout.innerHTML = `
                <span style="font-size: 1rem; line-height: 1;">✨</span>
                <div>
                    <strong style="color: #15803d; display: block; margin-bottom: 2px; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.05em;">Why this matches:</strong>
                    <span style="line-height: 1.4;">\${escapeHtml(job.reason)}</span>
                </div>
            `;
            leftContainer.appendChild(reasonCallout);
        }

        card.appendChild(leftContainer);

        // Right Container (Action Buttons)
        const rightContainer = document.createElement('div');
        rightContainer.style.display = 'flex';
        rightContainer.style.gap = '0.5rem';
        rightContainer.style.alignSelf = 'center';

        if (job.alreadyApplied) {
            const appliedBtn = document.createElement('button');
            appliedBtn.className = 'btn';
            appliedBtn.disabled = true;
            appliedBtn.style.background = '#e2e8f0';
            appliedBtn.style.color = '#64748b';
            appliedBtn.style.border = '1px solid #cbd5e1';
            appliedBtn.style.cursor = 'not-allowed';
            appliedBtn.innerText = 'Applied';
            rightContainer.appendChild(appliedBtn);
        } else if (!job.hasResume) {
            const uploadBtn = document.createElement('a');
            uploadBtn.href = '${pageContext.request.contextPath}/student/profile';
            uploadBtn.onclick = function(e) { e.stopPropagation(); };
            uploadBtn.title = 'Upload a resume on your profile page first';
            uploadBtn.style.display = 'inline-block';
            uploadBtn.style.padding = '8px 16px';
            uploadBtn.style.borderRadius = '6px';
            uploadBtn.style.fontSize = '0.875rem';
            uploadBtn.style.fontWeight = '500';
            uploadBtn.style.background = '#fef3c7';
            uploadBtn.style.color = '#92400e';
            uploadBtn.style.border = '1px solid #f59e0b';
            uploadBtn.style.textDecoration = 'none';
            uploadBtn.innerText = 'Upload Resume First';
            rightContainer.appendChild(uploadBtn);
        } else {
            const applyBtn = document.createElement('button');
            applyBtn.id = 'apply-btn-' + job.jobId;
            applyBtn.className = 'btn btn-primary';
            applyBtn.style.background = '#10b981';
            applyBtn.style.border = '1px solid #10b981';
            applyBtn.onclick = function(e) {
                e.stopPropagation();
                quickApply(job.jobId);
            };
            applyBtn.innerText = 'Apply';
            rightContainer.appendChild(applyBtn);
        }

        const detailsBtn = document.createElement('button');
        detailsBtn.className = 'btn btn-primary';
        detailsBtn.style.background = 'transparent';
        detailsBtn.style.border = '1px solid var(--primary)';
        detailsBtn.style.color = 'var(--primary)';
        detailsBtn.onclick = function(e) {
            e.stopPropagation();
            window.location.href = '${pageContext.request.contextPath}/student/jobs/' + job.jobId;
        };
        detailsBtn.innerText = 'View Details';
        rightContainer.appendChild(detailsBtn);

        card.appendChild(rightContainer);
        jobsGrid.appendChild(card);
    });

    jobsGrid.style.display = 'grid';
}

function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

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
