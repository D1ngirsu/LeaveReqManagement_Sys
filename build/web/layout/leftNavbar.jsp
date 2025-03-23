<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="sidebar" class="sidebar">
    <div class="sidebar-header">
        <div class="user-info">
            <c:if test="${not empty staff}">
                <div class="user-name">${staff.fullName}</div>
                <div class="user-role">
                    <c:choose>
                        <c:when test="${staff.roleId == 1}">Division Leader</c:when>
                        <c:when test="${staff.roleId == 2}">Tr??ng nhóm</c:when>
                        <c:otherwise>Employee</c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>
    
    <div class="sidebar-menu">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/leave/create">
                    <i class="menu-icon"></i>
                    <span>Create Leave Request</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/leave/list">
                    <i class="menu-icon"></i>
                    <span>List Leave Requests</span>
                </a>
            </li>
            
            <c:if test="${staff.roleId == 1 || staff.roleId == 2}">
                <li>
                    <a href="${pageContext.request.contextPath}/leave/review">
                        <i class="menu-icon"></i>
                        <span>List Request Review</span>
                    </a>
                </li>
            </c:if>
            
            <c:if test="${staff.roleId == 1}">
                <li>
                    <a href="${pageContext.request.contextPath}/agenda">
                        <i class="menu-icon"></i>
                        <span>Agenda</span>
                    </a>
                </li>
            </c:if>
            
            <li class="logout">
                <a href="${pageContext.request.contextPath}/logout">
                    <i class="menu-icon"></i>
                    <span>Logout</span>
                </a>
            </li>
        </ul>
    </div>
</div>

<div id="overlay" class="overlay"></div>

<button id="sidebarToggle" class="sidebar-toggle">
    <span></span>
</button>

<style>
    .sidebar {
        position: fixed;
        left: 0;
        top: 0;
        height: 100%;
        width: 250px;
        background-color: #2c3e50;
        color: #ecf0f1;
        transition: all 0.3s ease;
        z-index: 1001;
        box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
        overflow-y: auto;
        transform: translateX(-250px);
    }
    
    .sidebar-visible {
        transform: translateX(0);
    }
    
    .overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 1000;
        display: none;
    }
    
    .overlay-visible {
        display: block;
    }
    
    .sidebar-header {
        padding: 20px;
        border-bottom: 1px solid #34495e;
    }
    
    .user-info {
        text-align: center;
        padding: 10px 0;
    }
    
    .user-name {
        font-weight: 600;
        font-size: 1.1rem;
        margin-bottom: 5px;
    }
    
    .user-role {
        font-size: 0.9rem;
        color: #bdc3c7;
    }
    
    .sidebar-menu {
        padding: 20px 0;
    }
    
    .sidebar-menu ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }
    
    .sidebar-menu li {
        margin-bottom: 5px;
    }
    
    .sidebar-menu li a {
        display: flex;
        align-items: center;
        padding: 12px 20px;
        color: #ecf0f1;
        text-decoration: none;
        transition: all 0.3s ease;
    }
    
    .sidebar-menu li a:hover {
        background-color: #34495e;
    }
    
    .menu-icon {
        margin-right: 15px;
        font-size: 1.1rem;
    }
    
    .logout {
        margin-top: 30px;
        border-top: 1px solid #34495e;
        padding-top: 15px;
    }
    
    .logout a {
        color: #e74c3c;
    }
    
    .sidebar-toggle {
        position: fixed;
        top: 15px;
        left: 15px;
        z-index: 1002;
        background-color: #2c3e50;
        color: white;
        border: none;
        border-radius: 4px;
        padding: 8px 12px;
        cursor: pointer;
        transition: all 0.3s ease;
    }
</style>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        const sidebarToggle = document.getElementById('sidebarToggle');
        
        // Check for saved state
        const sidebarVisible = localStorage.getItem('sidebarVisible') === 'true';
        
        // Set initial state
        if (sidebarVisible) {
            sidebar.classList.add('sidebar-visible');
            overlay.classList.add('overlay-visible');
        }
        
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('sidebar-visible');
            overlay.classList.toggle('overlay-visible');
            
            // Save state
            localStorage.setItem('sidebarVisible', sidebar.classList.contains('sidebar-visible'));
        });
        
        // Close sidebar when clicking overlay
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('sidebar-visible');
            overlay.classList.remove('overlay-visible');
            
            // Save state
            localStorage.setItem('sidebarVisible', false);
        });
        
        // Close sidebar when pressing Escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && sidebar.classList.contains('sidebar-visible')) {
                sidebar.classList.remove('sidebar-visible');
                overlay.classList.remove('overlay-visible');
                
                // Save state
                localStorage.setItem('sidebarVisible', false);
            }
        });
    });
</script>