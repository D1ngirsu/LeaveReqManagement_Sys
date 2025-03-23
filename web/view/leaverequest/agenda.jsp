<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Staff Agenda</title>
        <style>
            table {
                border-collapse: collapse;
                width: 100%;
                max-width: 1000px;
            }

            th, td {
                border: 1px solid black;
                padding: 8px;
                text-align: center;
            }

            th {
                background-color: #f2f2f2;
            }

            .status-0 {
                background-color: #FFFFFF; /* Default - Available */
            }

            .status-1 {
                background-color: #FF0000; /* Red - Requested/Pending */
            }

            .status-2 {
                background-color: #FFFF00; /* Yellow - Under Review */
            }

            .status-3 {
                background-color: #90EE90; /* Light Green - Approved */
            }

            .status-4 {
                background-color: #A9A9A9; /* Gray - Rejected */
            }

            .date-filter {
                margin-bottom: 20px;
            }

            .legend {
                margin-top: 20px;
                border: 1px solid #ddd;
                padding: 10px;
                display: inline-block;
            }

            .legend-item {
                display: inline-block;
                margin-right: 15px;
            }

            .color-box {
                display: inline-block;
                width: 20px;
                height: 20px;
                margin-right: 5px;
                vertical-align: middle;
            }

            .container {
                margin: 20px;
            }

            h1 {
                color: #333;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Phòng ban Agenda</h1>

            <c:if test="${not empty errorMessage}">
                <div style="color: red; margin-bottom: 15px;">
                    ${errorMessage}
                </div>
            </c:if>

            <div class="date-filter">
                <form action="agenda" method="GET">
                    Từ ngày: <input type="date" name="startDate" value="${param.startDate}">
                    Đến ngày: <input type="date" name="endDate" value="${param.endDate}">
                    <button type="submit">Xem</button>
                </form>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Nhân sự</th>
                            <c:forEach var="date" items="${displayDates}">
                            <th><fmt:formatDate value="${date}" pattern="d/M" /></th>
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
                                    <c:set var="status" value="0" /> <!-- Default status: Available -->
                                </c:if>
                                <td class="status-${status}"></td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="legend">
                <h3>Chú thích:</h3>
                <div class="legend-item">
                    <div class="color-box status-0"></div> Có mặt
                </div>
                <div class="legend-item">
                    <div class="color-box status-1"></div> Đã yêu cầu nghỉ
                </div>
                <div class="legend-item">
                    <div class="color-box status-2"></div> Đang xem xét
                </div>
                <div class="legend-item">
                    <div class="color-box status-3"></div> Đã duyệt
                </div>
                <div class="legend-item">
                    <div class="color-box status-4"></div> Từ chối
                </div>
            </div>
        </div>
    </body>
</html>