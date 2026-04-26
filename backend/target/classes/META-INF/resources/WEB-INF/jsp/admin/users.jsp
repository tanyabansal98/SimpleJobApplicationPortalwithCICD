<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="title" value="User Management"/>
</jsp:include>

<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h1>User Management</h1>
        <p class="muted">Manage platform access and roles</p>
    </div>

    <table style="width: 100%; border-collapse: collapse;">
        <thead>
            <tr style="border-bottom: 2px solid var(--border); text-align: left;">
                <th style="padding: 1rem 0;">ID</th>
                <th style="padding: 1rem 0;">Email</th>
                <th style="padding: 1rem 0;">Role</th>
                <th style="padding: 1rem 0;">Status</th>
                <th style="padding: 1rem 0;">Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${users}">
                <c:if test="${not empty u.userId and not empty u.email}">
                    <tr style="border-bottom: 1px solid var(--border);">
                        <td style="padding: 1rem 0;">${u.userId}</td>
                        <td style="padding: 1rem 0; font-weight: 500;">${u.email}</td>
                        <td style="padding: 1rem 0;">
                            <span style="font-size: 0.875rem; font-weight: 600;">${u.role}</span>
                        </td>
                        <td style="padding: 1rem 0;">
                            <span style="color: ${u.active ? 'var(--success)' : 'var(--danger)'}; font-weight: 600;">
                                ${u.active ? 'Active' : 'Deactivated'}
                            </span>
                        </td>
                        <td style="padding: 1rem 0;">
                            <form action="${pageContext.request.contextPath}/admin/users/${u.active ? 'deactivate' : 'activate'}" method="post" style="display: inline;">
                                <input type="hidden" name="userId" value="${u.userId}">
                                <button type="submit" class="btn" style="padding: 4px 12px; font-size: 0.75rem; 
                                    background: ${u.active ? '#fee2e2' : '#dcfce7'}; 
                                    color: ${u.active ? 'var(--danger)' : 'var(--success)'};">
                                    ${u.active ? 'Deactivate' : 'Activate'}
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="../common/footer.jsp"/>
