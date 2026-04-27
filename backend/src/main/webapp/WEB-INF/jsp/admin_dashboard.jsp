<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 3rem;
        }
        .stat-card {
            padding: 1.5rem;
            text-align: center;
        }
        .stat-value {
            font-size: 2rem;
            font-weight: 800;
            color: var(--primary);
            margin-top: 0.5rem;
        }
        .stat-label {
            font-size: 0.75rem;
            text-transform: uppercase;
            font-weight: 700;
            color: var(--text-muted);
            letter-spacing: 0.05em;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            background: var(--white);
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        th {
            background-color: var(--header);
            color: white;
            text-align: left;
            padding: 1rem;
            font-size: 0.875rem;
        }
        td {
            padding: 1rem;
            border-bottom: 1px solid var(--border);
            font-size: 0.875rem;
        }
        .role-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 700;
        }
        .badge-student { background: #e0f2fe; color: #0369a1; }
        .badge-employer { background: #fef3c7; color: #92400e; }
        .badge-admin { background: #fee2e2; color: #991b1b; }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/dashboard" class="logo">JobApplicationPortal</a>
            <nav>
                <span style="margin-right: 1rem; opacity: 0.8;">Admin: ${sessionScope.user.email}</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary" style="color: #333; padding: 0.5rem 1rem;">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container" style="padding-top: 3rem;">
        <div class="mb-8">
            <h1>Admin Command Center</h1>
            <p class="muted">Global system oversight and intelligence overview</p>
        </div>

        <div class="stats-grid">
            <div class="card stat-card">
                <div class="stat-label">Total Users</div>
                <div class="stat-value">${stats.totalUsers}</div>
            </div>
            <div class="card stat-card">
                <div class="stat-label">Active Jobs</div>
                <div class="stat-value">${stats.activeJobs}</div>
            </div>
            <div class="card stat-card">
                <div class="stat-label">Total Apps</div>
                <div class="stat-value">${stats.totalApplications}</div>
            </div>
        </div>

        <div class="card" style="padding: 0;">
            <div style="padding: 1.5rem; border-bottom: 1px solid var(--border);">
                <h3 style="margin: 0;">User Directory</h3>
            </div>
            <div style="overflow-x: auto;">
                <table>
                    <thead>
                        <tr>
                            <th>Email Address</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td style="font-weight: 600;">${u.email}</td>
                                <td>
                                    <span class="role-badge badge-${u.role.toLowerCase()}">${u.role}</span>
                                </td>
                                <td>
                                    <span style="color: ${u.isActive ? '#10b981' : '#ef4444'}; font-weight: 700;">
                                        ${u.isActive ? 'Active' : 'Deactivated'}
                                    </span>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/toggle-user" method="post">
                                        <input type="hidden" name="userId" value="${u.userId}">
                                        <button type="submit" class="btn btn-secondary" style="font-size: 0.75rem; padding: 0.375rem 0.75rem;">
                                            ${u.isActive ? 'Deactivate' : 'Activate'}
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
