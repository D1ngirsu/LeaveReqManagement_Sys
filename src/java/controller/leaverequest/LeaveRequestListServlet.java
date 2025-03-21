package controller.leaverequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import dao.LeaveRequestDAO;
import model.LeaveRequest;
import model.Staff;

@WebServlet(name = "LeaveRequestListServlet", urlPatterns = {"/leave/list"})
public class LeaveRequestListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        Staff currentUser = (Staff) session.getAttribute("staff");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get search and filter parameters
        String title = request.getParameter("title");
        Date fromDate = null;
        Date toDate = null;
        Integer status = null;
        
        // Parse date parameters if provided
        try {
            if (request.getParameter("fromDate") != null && !request.getParameter("fromDate").isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = sdf.parse(request.getParameter("fromDate"));
                fromDate = new Date(parsedDate.getTime());
            }
            
            if (request.getParameter("toDate") != null && !request.getParameter("toDate").isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = sdf.parse(request.getParameter("toDate"));
                toDate = new Date(parsedDate.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        // Parse status parameter if provided
        if (request.getParameter("status") != null && !request.getParameter("status").isEmpty()) {
            try {
                status = Integer.parseInt(request.getParameter("status"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        // Get pagination parameters
        int page = 1;
        int pageSize = 10; // Default page size
        
        if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        if (request.getParameter("pageSize") != null && !request.getParameter("pageSize").isEmpty()) {
            try {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
                if (pageSize < 1) pageSize = 10;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Lấy danh sách leave request của người dùng hiện tại với lọc và phân trang
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        List<LeaveRequest> leaveRequests = leaveRequestDAO.getLeaveRequestsByOwner(
            currentUser.getId(), title, fromDate, toDate, status, page, pageSize
        );
        
        // Get total count for pagination
        int totalRequests = leaveRequestDAO.countLeaveRequestsByOwner(
            currentUser.getId(), title, fromDate, toDate, status
        );
        
        int totalPages = (int) Math.ceil((double) totalRequests / pageSize);

        // Thêm danh sách request và thông tin phân trang vào request scope
        request.setAttribute("leaveRequests", leaveRequests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        
        // Set back search parameters for form
        request.setAttribute("title", title);
        request.setAttribute("fromDate", request.getParameter("fromDate"));
        request.setAttribute("toDate", request.getParameter("toDate"));
        request.setAttribute("status", status);

        // Chuyển đến trang JSP hiển thị danh sách
        request.getRequestDispatcher("/view/leaverequest/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle search form submission
        // We'll just redirect to doGet with the search parameters
        
        String title = request.getParameter("title");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String status = request.getParameter("status");
        
        StringBuilder redirectURL = new StringBuilder(request.getContextPath() + "/leave/list?");
        
        if (title != null && !title.isEmpty()) {
            redirectURL.append("title=").append(title).append("&");
        }
        
        if (fromDate != null && !fromDate.isEmpty()) {
            redirectURL.append("fromDate=").append(fromDate).append("&");
        }
        
        if (toDate != null && !toDate.isEmpty()) {
            redirectURL.append("toDate=").append(toDate).append("&");
        }
        
        if (status != null && !status.isEmpty()) {
            redirectURL.append("status=").append(status).append("&");
        }
        
        response.sendRedirect(redirectURL.toString());
    }
}