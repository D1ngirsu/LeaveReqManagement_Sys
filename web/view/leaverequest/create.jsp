<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create Leave Request</title>
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
            }
            .container {
                width: 100%;
                max-width: 600px;
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
            .error-message {
                background-color: #ffebee;
                color: #d32f2f;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                text-align: center;
            }
            .form-group {
                margin-bottom: 1.5rem;
            }
            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                color: #34495e;
                font-weight: 500;
            }
            .form-group input, 
            .form-group textarea, 
            .form-group select {
                width: 100%;
                padding: 0.75rem;
                border: 1px solid #ddd;
                border-radius: 8px;
                font-size: 1rem;
                transition: border-color 0.3s ease;
            }
            .form-group input:focus, 
            .form-group textarea:focus, 
            .form-group select:focus {
                outline: none;
                border-color: #3498db;
                box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
            }
            .form-group small {
                color: #7f8c8d;
                display: block;
                margin-top: 0.5rem;
            }
            .btn-submit, .btn-cancel {
                display: inline-block;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            .btn-submit {
                background-color: #3498db;
                color: white;
                border: none;
                margin-right: 1rem;
                cursor: pointer;
            }
            .btn-submit:hover {
                background-color: #2980b9;
            }
            .btn-cancel {
                background-color: #ecf0f1;
                color: #2c3e50;
            }
            .btn-cancel:hover {
                background-color: #dfe4ea;
            }
            .form-group .btn-container {
                display: flex;
                justify-content: center;
                margin-top: 1.5rem;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <jsp:include page="../../layout/leftNavbar.jsp" />
            <h1>Create New Leave Request</h1>

            <c:if test="${not empty error}">
                <div class="error-message">
                    ${error}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/leave/create" method="POST">
                <div class="form-group">
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" required placeholder="Enter leave request title"/>
                </div>

                <div class="form-group">
                    <label for="reason">Reason:</label>
                    <textarea id="reason" name="reason" rows="4" required placeholder="Explain your reason for leave"></textarea>
                </div>

                <div class="form-group">
                    <label for="from">From Date:</label>
                    <input type="date" id="from" name="from" required/>
                </div>

                <div class="form-group">
                    <label for="to">To Date:</label>
                    <input type="date" id="to" name="to" required/>
                </div>

                <div class="form-group">
                    <label for="eid">Owner:</label>
                    <select id="eid" name="eid">
                        <option value="">-- Select Employee --</option>
                        <c:forEach items="${employees}" var="e">
                            <option value="${e.id}"
                                    <c:if test="${e.id eq sessionScope.user.staff.id}">
                                        selected="selected"
                                    </c:if>
                                    >
                                ${e.fullName}
                            </option>
                        </c:forEach>
                    </select>
                    <small>If not selected, you will be set as the owner</small>
                </div>

                <div class="form-group btn-container">
                    <input type="submit" value="Submit Request" class="btn-submit"/>
                    <a href="${pageContext.request.contextPath}/leave/list" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>

        <script>
            document.querySelector('form').addEventListener('submit', function (e) {
                var fromDate = new Date(document.getElementById('from').value);
                var toDate = new Date(document.getElementById('to').value);

                if (fromDate > toDate) {
                    e.preventDefault();
                    alert('From date must be before To date');
                }
            });
        </script>
    </body>
</html>