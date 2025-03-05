<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Leave Requests</title>
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
                padding: 2rem;
            }
            .container {
                background-color: white;
                border-radius: 12px;
                box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
                padding: 2rem;
            }
            h1 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 1.5rem;
                font-weight: 600;
                letter-spacing: -0.5px;
            }
            .action-links {
                margin-bottom: 1.5rem;
                text-align: right;
            }
            .action-links a {
                display: inline-block;
                background-color: #3498db;
                color: white;
                text-decoration: none;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                transition: background-color 0.3s ease;
                font-weight: 600;
            }
            .action-links a:hover {
                background-color: #2980b9;
            }
            .success-message {
                background-color: #e8f5e9;
                color: #2ecc71;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                text-align: center;
            }
            .empty-message {
                text-align: center;
                color: #7f8c8d;
                padding: 2rem;
                background-color: #f8f9fa;
                border-radius: 8px;
            }
            table {
                width: 100%;
                border-collapse: separate;
                border-spacing: 0;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            }
            th, td {
                padding: 1rem;
                text-align: left;
                border-bottom: 1px solid #e9ecef;
            }
            th {
                background-color: #f8f9fa;
                color: #2c3e50;
                font-weight: 600;
            }
            tbody tr:hover {
                background-color: #f1f3f5;
            }
            .status-pending {
                color: #f39c12;
                background-color: #fef6e6;
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-weight: 500;
            }
            .status-approved {
                color: #2ecc71;
                background-color: #e8f5e9;
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-weight: 500;
            }
            .status-rejected {
                color: #e74c3c;
                background-color: #fdedec;
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-weight: 500;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>My Leave Requests</h1>

            <div class="action-links">
                <a href="${pageContext.request.contextPath}/leave/create">Create New Leave Request</a>
            </div>

            <c:if test="${not empty param.message}">
                <div class="success-message">
                    <c:choose>
                        <c:when test="${param.message == 'created'}">Leave request created successfully.</c:when>
                        <c:when test="${param.message == 'updated'}">Leave request updated successfully.</c:when>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${empty leaveRequests}">
                <div class="empty-message">
                    <p>You have no leave requests.</p>
                </div>
            </c:if>

            <c:if test="${not empty leaveRequests}">
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Reason</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                            <th>Created By</th>
                            <th>Created Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="request" items="${leaveRequests}">
                            <tr>
                                <td>${request.title}</td>
                                <td>${request.reason}</td>
                                <td><fmt:formatDate value="${request.startDate}" pattern="yyyy-MM-dd"/></td>
                                <td><fmt:formatDate value="${request.endDate}" pattern="yyyy-MM-dd"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${request.status == 1}">
                                            <span class="status-pending">Pending</span>
                                        </c:when>
                                        <c:when test="${request.status == 2}">
                                            <span class="status-approved">Approved</span>
                                        </c:when>
                                        <c:when test="${request.status == 0}">
                                            <span class="status-rejected">Rejected</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>${request.createdBy.fullName}</td>
                                <td><fmt:formatDate value="${request.createddate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${request.status == 1}">
                                            <a href="${pageContext.request.contextPath}/leave/edit?rid=${request.rid}" class="edit-btn">Edit</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="edit-btn edit-btn-disabled">Edit</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </body>
</html>