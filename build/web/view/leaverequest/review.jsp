<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Leave Requests</title>
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
        .error {
            background-color: #ffebee;
            color: #d32f2f;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        .success-message {
            background-color: #e8f5e9;
            color: #2e7d32;
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
            margin-top: 1.5rem;
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
        .review-form {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .review-form select, 
        .review-form input[type="submit"] {
            padding: 0.5rem;
            border-radius: 4px;
            border: 1px solid #ddd;
        }
        .review-form input[type="submit"] {
            background-color: #3498db;
            color: white;
            border: none;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .review-form input[type="submit"]:hover {
            background-color: #2980b9;
        }
        .search-form {
            background-color: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        .form-group {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }
        .form-control {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        .form-control label {
            font-weight: 500;
            color: #2c3e50;
        }
        .form-control input,
        .form-control select {
            padding: 0.75rem;
            border-radius: 4px;
            border: 1px solid #ddd;
            font-size: 1rem;
            width: 100%;
        }
        .form-submit {
            display: flex;
            justify-content: flex-end;
            gap: 1rem;
        }
        .btn {
            padding: 0.75rem 1.5rem;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .btn-primary {
            background-color: #3498db;
            color: white;
            border: none;
        }
        .btn-primary:hover {
            background-color: #2980b9;
        }
        .btn-secondary {
            background-color: #ecf0f1;
            color: #2c3e50;
            border: 1px solid #ddd;
        }
        .btn-secondary:hover {
            background-color: #dfe6e9;
        }
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 2rem;
            gap: 0.5rem;
        }
        .pagination a, .pagination span {
            padding: 0.5rem 1rem;
            border-radius: 4px;
            text-decoration: none;
            color: #3498db;
            background-color: #f8f9fa;
            border: 1px solid #ddd;
            transition: all 0.3s ease;
        }
        .pagination a:hover {
            background-color: #3498db;
            color: white;
        }
        .pagination .active {
            background-color: #3498db;
            color: white;
        }
        .pagination .disabled {
            color: #bdc3c7;
            pointer-events: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Review Leave Requests</h1>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        
        <c:if test="${param.message eq 'updated'}">
            <div class="success-message">Leave request has been successfully updated.</div>
        </c:if>

        <!-- Search Form -->
        <form class="search-form" action="${pageContext.request.contextPath}/leave/review" method="get">
            <div class="form-group">
                <div class="form-control">
                    <label for="title">Title</label>
                    <input type="text" id="title" name="title" value="${title}" placeholder="Search by title">
                </div>
                
                <div class="form-control">
                    <label for="createdBy">Created By</label>
                    <input type="text" id="createdBy" name="createdBy" value="${createdBy}" placeholder="Search by creator">
                </div>
                
                <div class="form-control">
                    <label for="ownerName">Owner</label>
                    <input type="text" id="ownerName" name="ownerName" value="${ownerName}" placeholder="Search by owner">
                </div>
                
                <div class="form-control">
                    <label for="status">Status</label>
                    <select id="status" name="status">
                        <option value="">All Status</option>
                        <option value="1" ${status == 1 ? 'selected' : ''}>Pending</option>
                        <option value="2" ${status == 2 ? 'selected' : ''}>Approved</option>
                        <option value="0" ${status == 0 ? 'selected' : ''}>Rejected</option>
                    </select>
                </div>
            </div>
            
            <div class="form-group">
                <div class="form-control">
                    <label for="fromDate">From Date</label>
                    <input type="date" id="fromDate" name="fromDate" value="${fromDate}">
                </div>
                
                <div class="form-control">
                    <label for="toDate">To Date</label>
                    <input type="date" id="toDate" name="toDate" value="${toDate}">
                </div>
                
                <div class="form-control">
                    <label for="pageSize">Items Per Page</label>
                    <select id="pageSize" name="pageSize">
                        <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                        <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                        <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                        <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                    </select>
                </div>
            </div>
            
            <div class="form-submit">
                <button type="reset" class="btn btn-secondary">Reset</button>
                <button type="submit" class="btn btn-primary">Search</button>
            </div>
        </form>

        <c:if test="${empty leaveRequests}">
            <div class="empty-message">
                <p>No leave requests found matching your criteria.</p>
            </div>
        </c:if>

        <c:if test="${not empty leaveRequests}">
            <table>
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Owner</th>
                        <th>Created By</th>
                        <th>Reason</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="request" items="${leaveRequests}">
                        <tr>
                            <td>${request.title}</td>
                            <td>${request.owner.fullName} (${request.owner.roleName})</td>
                            <td>${request.createdBy.fullName}</td>
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
                            <td>
                                <c:if test="${request.status == 1}">
                                    <form class="review-form" action="${pageContext.request.contextPath}/leave/review" method="post">
                                        <input type="hidden" name="requestId" value="${request.rid}">
                                        <select name="status">
                                            <option value="2">Approve</option>
                                            <option value="0">Reject</option>
                                        </select>
                                        <input type="submit" value="Submit">
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
            <!-- Pagination -->
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="${pageContext.request.contextPath}/leave/review?page=${currentPage - 1}&pageSize=${pageSize}&title=${title}&createdBy=${createdBy}&ownerName=${ownerName}&status=${status}&fromDate=${fromDate}&toDate=${toDate}">Previous</a>
                </c:if>
                
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span class="active">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/leave/review?page=${i}&pageSize=${pageSize}&title=${title}&createdBy=${createdBy}&ownerName=${ownerName}&status=${status}&fromDate=${fromDate}&toDate=${toDate}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                
                <c:if test="${currentPage < totalPages}">
                    <a href="${pageContext.request.contextPath}/leave/review?page=${currentPage + 1}&pageSize=${pageSize}&title=${title}&createdBy=${createdBy}&ownerName=${ownerName}&status=${status}&fromDate=${fromDate}&toDate=${toDate}">Next</a>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>