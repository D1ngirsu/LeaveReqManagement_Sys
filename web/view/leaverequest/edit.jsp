<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Leave Request</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Inter', sans-serif;
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
            max-width: 600px;
            margin: 0 auto;
        }
        h1 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 1.5rem;
            font-weight: 600;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }
        .form-group input, 
        .form-group select, 
        .form-group textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 8px;
        }
        .submit-btn {
            display: block;
            width: 100%;
            padding: 1rem;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .submit-btn:hover {
            background-color: #2980b9;
        }
        .error-message {
            background-color: #fdedec;
            color: #e74c3c;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <jsp:include page="../../layout/leftNavbar.jsp" />
        <h1>Edit Leave Request</h1>

        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/leave/edit" method="post">
            <input type="hidden" name="rid" value="${leaveRequest.rid}">

            <div class="form-group">
                <label for="title">Title</label>
                <input type="text" id="title" name="title" value="${leaveRequest.title}" required>
            </div>

            <div class="form-group">
                <label for="reason">Reason</label>
                <textarea id="reason" name="reason" rows="4" required>${leaveRequest.reason}</textarea>
            </div>

            <div class="form-group">
                <label for="from">From Date</label>
                <input type="date" id="from" name="from" 
                       value="<fmt:formatDate value='${leaveRequest.startDate}' pattern='yyyy-MM-dd'/>" required>
            </div>

            <div class="form-group">
                <label for="to">To Date</label>
                <input type="date" id="to" name="to" 
                       value="<fmt:formatDate value='${leaveRequest.endDate}' pattern='yyyy-MM-dd'/>" required>
            </div>

            <button type="submit" class="submit-btn">Update Leave Request</button>
        </form>
    </div>
</body>
</html>