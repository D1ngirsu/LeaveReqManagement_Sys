<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Staff Agenda</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                background-color: #f4f7f6;
                line-height: 1.6;
                color: #333;
                padding: 20px;
            }
            
            .container {
                width: 100%;
                max-width: 1200px;
                margin: 2rem auto;
                background-color: white;
                padding: 2rem;
                border-radius: 12px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
            }
            
            h1 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 1.5rem;
                font-weight: 600;
            }
            
            h3 {
                color: #2c3e50;
                margin-bottom: 0.5rem;
                font-weight: 500;
            }
            
            .error-message {
                background-color: #ffebee;
                color: #d32f2f;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                text-align: center;
            }
            
            .warning {
                background-color: #fff8e1;
                color: #ff9800;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                text-align: center;
            }
            
            .table-container {
                width: 100%;
                overflow-x: auto;
                margin-bottom: 1.5rem;
                border-radius: 8px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            }
            
            table {
                border-collapse: collapse;
                width: 100%;
                min-width: 100%;
                border-radius: 8px;
                overflow: hidden;
            }

            th, td {
                border: 1px solid #ddd;
                padding: 12px;
                text-align: center;
                min-width: 70px; /* Ensure columns have a minimum width */
            }
            
            th:first-child, td:first-child {
                position: sticky;
                left: 0;
                background-color: white;
                z-index: 10;
                border-right: 2px solid #ddd;
                min-width: 150px;
            }
            
            th:first-child {
                background-color: #34495e;
            }

            th {
                background-color: #34495e;
                color: white;
                font-weight: 500;
            }

            /* Updated status colors as requested */
            .status-0 {
                background-color: #CFD8DC; /* Gray - Rejected */
            }

            .status-1 {
                background-color: #ffcdd2; /* Soft Red - Pending */
            }

            .status-2 {
                background-color: #c8e6c9; /* Soft Green - Approved */
            }
            
            .status-3 {
                background-color: #fff9c4; /* Soft Yellow - Under Review (keeping for backward compatibility) */
            }
            
            .status-4 {
                background-color: #FFFFFF; /* White - Available (keeping for backward compatibility) */
            }

            .today {
                border: 2px solid #3498db !important;
                font-weight: bold;
            }

            .date-filter {
                margin-bottom: 1.5rem;
                background-color: #f8f9fa;
                padding: 1.5rem;
                border-radius: 8px;
            }
            
            .date-filter form {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                align-items: center;
            }
            
            .date-filter input {
                padding: 0.75rem;
                border: 1px solid #ddd;
                border-radius: 8px;
                font-size: 1rem;
                transition: border-color 0.3s ease;
            }
            
            .date-filter input:focus {
                outline: none;
                border-color: #3498db;
                box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
            }

            .btn {
                display: inline-block;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                transition: all 0.3s ease;
                border: none;
                cursor: pointer;
                font-size: 1rem;
            }
            
            .btn-primary {
                background-color: #3498db;
                color: white;
            }
            
            .btn-primary:hover {
                background-color: #2980b9;
            }
            
            .btn-secondary {
                background-color: #ecf0f1;
                color: #2c3e50;
            }
            
            .btn-secondary:hover {
                background-color: #dfe4ea;
            }
            
            .month-nav {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 1.5rem;
                background-color: #f8f9fa;
                padding: 1rem;
                border-radius: 8px;
            }
            
            .month-nav .month-display {
                font-size: 1.2rem;
                font-weight: 600;
                color: #2c3e50;
            }
            
            .legend {
                margin-top: 1.5rem;
                border: 1px solid #ddd;
                padding: 1.5rem;
                border-radius: 8px;
                background-color: #f8f9fa;
            }

            .legend-items {
                display: flex;
                flex-wrap: wrap;
                gap: 15px;
                margin-top: 10px;
            }

            .legend-item {
                display: flex;
                align-items: center;
                margin-right: 15px;
            }

            .color-box {
                display: inline-block;
                width: 20px;
                height: 20px;
                margin-right: 8px;
                border-radius: 4px;
                border: 1px solid #ddd;
            }
            
            .today-box {
                border: 2px solid #3498db;
                width: 20px;
                height: 20px;
                margin-right: 8px;
                border-radius: 4px;
                display: inline-block;
            }
            
            /* Scrollbar styling */
            .table-container::-webkit-scrollbar {
                height: 10px;
            }
            
            .table-container::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 10px;
            }
            
            .table-container::-webkit-scrollbar-thumb {
                background: #bbb;
                border-radius: 10px;
            }
            
            .table-container::-webkit-scrollbar-thumb:hover {
                background: #999;
            }
            
            /* Scroll indicator */
            .scroll-indicator {
                display: none;
                text-align: center;
                margin-bottom: 10px;
                color: #666;
                font-size: 0.9rem;
            }
            
            @media (max-width: 768px) {
                .month-nav, .date-filter form {
                    flex-direction: column;
                    align-items: flex-start;
                }
                
                .date-filter form > * {
                    width: 100%;
                }
                
                .scroll-indicator {
                    display: block;
                }
            }
        </style>
    </head>
    <body>
        <div class="container">
            <jsp:include page="../../layout/leftNavbar.jsp" />
            <h1>Staff Agenda</h1>

            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    ${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty warningMessage}">
                <div class="warning">
                    ${warningMessage}
                </div>
            </c:if>
            
            <c:if test="${!customDateRange}">
                <!-- Month Navigation -->
                <div class="month-nav">
                    <form action="agenda" method="GET">
                        <input type="hidden" name="month" value="${currentMonth}">
                        <input type="hidden" name="year" value="${currentYear}">
                        <input type="hidden" name="nav" value="prev">
                        <button type="submit" class="btn btn-secondary">« Previous Month</button>
                    </form>
                    
                    <div class="month-display">${monthYearDisplay}</div>
                    
                    <form action="agenda" method="GET">
                        <input type="hidden" name="month" value="${currentMonth}">
                        <input type="hidden" name="year" value="${currentYear}">
                        <input type="hidden" name="nav" value="next">
                        <button type="submit" class="btn btn-secondary">Next Month »</button>
                    </form>
                </div>
            </c:if>

            <div class="date-filter">
                <form action="agenda" method="GET">
                    <c:choose>
                        <c:when test="${customDateRange}">
                            <label for="startDate">From:</label>
                            <input type="date" id="startDate" name="startDate" value="${startDate}" required>
                            
                            <label for="endDate">To:</label>
                            <input type="date" id="endDate" name="endDate" value="${endDate}" required>
                        </c:when>
                        <c:otherwise>
                            <label for="startDate">From:</label>
                            <input type="date" id="startDate" name="startDate" required>
                            
                            <label for="endDate">To:</label>
                            <input type="date" id="endDate" name="endDate" required>
                        </c:otherwise>
                    </c:choose>
                    <button type="submit" class="btn btn-primary">View Custom Range</button>
                    <c:if test="${customDateRange}">
                        <a href="agenda" class="btn btn-secondary">Return to Current Month</a>
                    </c:if>
                </form>
            </div>

            <div class="scroll-indicator">← Scroll horizontally to view all dates →</div>
            
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Staff</th>
                            <c:forEach var="date" items="${displayDates}">
                                <c:set var="dateStr"><fmt:formatDate value="${date}" pattern="yyyy-MM-dd" /></c:set>
                                <th class="${dateStr eq today ? 'today' : ''}">
                                    <fmt:formatDate value="${date}" pattern="d/M" />
                                </th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="staff" items="${staffList}">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty staff.fullName}">
                                            ${staff.fullName}
                                        </c:when>
                                        <c:otherwise>
                                            Mr ${staff.username}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <c:forEach var="date" items="${displayDates}">
                                    <c:set var="dateStr"><fmt:formatDate value="${date}" pattern="yyyy-MM-dd" /></c:set>
                                    <c:set var="status" value="${staffLeaveStatus[staff.id][dateStr]}" />
                                    <c:if test="${empty status}">
                                        <c:set var="status" value="4" /> <!-- Default status: Available (now 4) -->
                                    </c:if>
                                    <td class="status-${status} ${dateStr eq today ? 'today' : ''}"></td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="legend">
                <h3>Legend</h3>
                <div class="legend-items">
                    <div class="legend-item">
                        <div class="color-box status-4"></div> Available
                    </div>
                    <div class="legend-item">
                        <div class="color-box status-0"></div> Rejected
                    </div>
                    <div class="legend-item">
                        <div class="color-box status-1"></div> Pending
                    </div>
                    <div class="legend-item">
                        <div class="color-box status-2"></div> Approved
                    </div>
                    <div class="legend-item">
                        <div class="today-box"></div> Today
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>