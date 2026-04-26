<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="Post New Job"/>
</jsp:include>

<div class="card" style="max-width: 800px; margin: 0 auto;">
    <div class="mb-8">
        <h1>Post a New Job</h1>
        <p class="muted">Fill in the details to attract the best candidates</p>
    </div>

    <form action="${pageContext.request.contextPath}/employer/jobs/new" method="post">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label class="label">Job Title</label>
                <input type="text" name="title" class="input" placeholder="e.g. Senior Software Engineer" required>
            </div>
            <div class="form-group">
                <label class="label">Company Name</label>
                <input type="text" name="companyName" class="input" placeholder="Your Company Name" required>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
            <div class="form-group">
                <label class="label">Job Type</label>
                <select name="jobType" class="input" required>
                    <option value="" disabled selected>Select type...</option>
                    <option value="Full Time">Full Time</option>
                    <option value="Internship">Internship</option>
                </select>
            </div>
            <div class="form-group">
                <label class="label">Location</label>
                <input type="text" name="location" class="input" placeholder="e.g. Boston, MA / Remote" required>
            </div>
            <div class="form-group">
                <label class="label">Salary Range</label>
                <input type="text" name="salaryRange" class="input" placeholder="e.g. $100k - $130k">
            </div>
        </div>

        <div class="form-group">
            <label class="label">Job Description</label>
            <textarea name="description" class="input" rows="6" placeholder="Describe the role, responsibilities, and requirements..." required></textarea>
        </div>

        <div class="form-group">
            <label class="label">Required Skills</label>
            <input type="text" name="requiredSkills" class="input" placeholder="e.g. Java, Spring Boot, SQL">
        </div>

        <div class="form-group">
            <label class="label">Benefits</label>
            <input type="text" name="benefits" class="input" placeholder="e.g. Health Insurance, 401k, PTO">
        </div>

        <div style="display: flex; gap: 1rem; margin-top: 2rem;">
            <button type="submit" class="btn btn-primary" style="flex: 1;">Post Job Posting</button>
            <a href="${pageContext.request.contextPath}/employer/dashboard" class="btn" style="background: #f1f5f9; color: #475569; flex: 1;">Cancel</a>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp"/>
