<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Home Page</title>
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
                display: flex;
            }
            .main-content {
                flex: 1;
                padding: 20px;
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
            .welcome-message {
                background-color: #e6f7ff;
                color: #0066cc;
                padding: 1rem;
                border-radius: 8px;
                margin-bottom: 1.5rem;
                text-align: center;
            }
            .btn-container {
                display: flex;
                justify-content: center;
                gap: 1rem;
                margin-top: 2rem;
            }
            .btn {
                display: inline-block;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                transition: all 0.3s ease;
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
            .content-section {
                margin-top: 2rem;
                line-height: 1.8;
            }
        </style>
    </head>
    <body>
        <!-- Include the left navbar -->
        <jsp:include page="/layout/leftNavbar.jsp" />
        
        <div class="main-content">
            <div class="container">
                <h1>Welcome to Leave Management System</h1>
                
                <div class="welcome-message">
                    <p>Hello, <span id="userName">${sessionScope.staff.fullName}</span>! Welcome to the system.</p>
                </div>
                
                <div class="content-section">
                    <p>From this portal, you can manage your leave requests, view your leave history, and check the status of your current requests.</p>
                    <p>Navigate using the sidebar menu to access different features.</p>
                </div>
                
            </div>
        </div>
    </body>
</html>