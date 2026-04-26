<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Upload Resume"/>
</jsp:include>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem;">
        <div>
            <h1>Upload Resume</h1>
            <p class="muted">Please upload your resume in PDF or DOCX format (Max 5MB)</p>
        </div>
        <a href="${pageContext.request.contextPath}/student/dashboard" class="btn" style="background: white; border: 1px solid var(--border); color: var(--primary);">Back to Dashboard</a>
    </div>

    <div id="documents-section">
        <div style="padding: 2rem; background: #f8fafc; border-radius: 12px; border: 1px solid var(--border);">
            <!-- Show current resume if exists -->
            <div id="resume-display" style="margin-bottom: 1.5rem; ${empty profile.resumeFileName ? 'display: none;' : ''}">
                <div style="padding: 1rem; background: white; border: 1px solid var(--border); border-radius: 8px;">
                    <div style="font-weight: 500;">Current Resume</div>
                    <div id="resume-name" class="muted" style="font-size: 0.875rem;"><c:out value="${profile.resumeFileName}"/></div>
                </div>
                <p class="muted" style="margin-top: 1rem; font-size: 0.875rem;">Resume has been successfully uploaded.</p>
            </div>

            <!-- Show upload section only if NO resume exists -->
            <div id="upload-container" style="${not empty profile.resumeFileName ? 'display: none;' : ''}">
                <div id="no-resume-msg" style="margin-bottom: 1.5rem;">
                    <p class="muted">No resume uploaded yet.</p>
                </div>

                <div style="display: flex; align-items: center; gap: 1rem;">
                    <input type="file" id="resumeFile" accept=".pdf,.docx" style="flex: 1; padding: 8px; background: white; border: 1px solid var(--border); border-radius: 6px;">
                    <button onclick="uploadResume()" class="btn btn-primary" id="uploadBtn">Upload Resume</button>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="notification" style="display: none; position: fixed; top: 20px; right: 20px; padding: 15px 25px; border-radius: 8px; color: white; font-weight: 500; z-index: 3000; transition: all 0.3s ease; box-shadow: 0 4px 12px rgba(0,0,0,0.15);"></div>

<script>
function showNotification(msg, isError) {
    const notification = document.getElementById('notification');
    notification.innerText = msg;
    notification.style.background = isError ? '#ef4444' : '#10b981';
    notification.style.display = 'block';
    setTimeout(() => { notification.style.display = 'none'; }, 3000);
}

function uploadResume() {
    const fileInput = document.getElementById('resumeFile');
    const file = fileInput.files[0];
    
    if (!file) {
        showNotification('Please select a file first.', true);
        return;
    }

    const btn = document.getElementById('uploadBtn');
    btn.disabled = true;
    btn.innerText = 'Uploading...';

    const formData = new FormData();
    formData.append('file', file);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '${pageContext.request.contextPath}/student/api/resume', true);
    
    xhr.onload = function() {
        btn.disabled = false;
        btn.innerText = 'Upload Resume';
        
        if (xhr.status === 200) {
            const data = JSON.parse(xhr.responseText);
            showNotification('Resume uploaded successfully!', false);
            
            // Success: Switch UI to display mode and hide upload
            document.getElementById('resume-display').style.display = 'block';
            document.getElementById('resume-name').innerText = data.fileName;
            document.getElementById('upload-container').style.display = 'none';
        } else {
            try {
                const data = JSON.parse(xhr.responseText);
                showNotification(data.message || 'Upload failed.', true);
            } catch(e) {
                showNotification('Error uploading file.', true);
            }
        }
    };

    xhr.onerror = function() {
        btn.disabled = false;
        btn.innerText = 'Upload Resume';
        showNotification('An unexpected error occurred.', true);
    };

    xhr.send(formData);
}
</script>

<jsp:include page="../common/footer.jsp"/>
