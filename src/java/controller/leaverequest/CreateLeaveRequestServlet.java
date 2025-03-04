package controller.leaverequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import dao.LeaveRequestDAO;
import dao.UserDAO;
import model.LeaveRequest;
import model.Staff;
import model.User;

@WebServlet(name = "CreateLeaveRequestServlet", urlPatterns = {"/leave/create"})
public class CreateLeaveRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("staff");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy danh sách nhân viên để hiển thị trong form
        UserDAO userDAO = new UserDAO();
        List<Staff> employees = userDAO.getAllStaff();
        request.setAttribute("employees", employees);

        // Forward đến trang JSP
        request.getRequestDispatcher("/view/leaverequest/create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("staff");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        System.out.println("Current user: " + user.getUsername() + ", ID: " + user.getId());

        try {
            // Lấy dữ liệu từ form
            String title = request.getParameter("title");
            String reason = request.getParameter("reason");
            Date fromDate = Date.valueOf(request.getParameter("from"));
            Date toDate = Date.valueOf(request.getParameter("to"));

            // Lấy giá trị ownerId, nếu không có hoặc rỗng, sử dụng ID của người đăng nhập
            String ownerIdParam = request.getParameter("eid");
            int ownerId;

            if (ownerIdParam == null || ownerIdParam.isEmpty()) {
                // Nếu không chọn, sử dụng người đăng nhập
                ownerId = user.getId(); // Hoặc cách khác tùy thuộc vào cấu trúc thực tế của User
            } else {
                ownerId = Integer.parseInt(ownerIdParam);
                // Nếu chọn chính mình, không cần thực hiện gì thêm vì đã parse ownerId ở trên
            }

            // Tạo đối tượng LeaveRequest
            LeaveRequest leaveRequest = new LeaveRequest();
            leaveRequest.setTitle(title);
            leaveRequest.setReason(reason);
            leaveRequest.setStartDate(fromDate);
            leaveRequest.setEndDate(toDate);

            // Thiết lập owner (nhân viên đăng ký nghỉ)
            UserDAO userDAO = new UserDAO();
            System.out.println("Owner ID selected: " + ownerId);
            Staff owner = userDAO.findStaffById(ownerId);
            System.out.println("Owner found: " + (owner != null ? owner.getUsername() : "null"));
            if (owner == null) {
                // Handle the case when staff is not found
                request.setAttribute("error", "Staff with ID " + ownerId + " not found. Please select a valid staff member.");
                doGet(request, response);
                return;
            } else {
                leaveRequest.setOwner(owner);
            }

            // Thiết lập người tạo request
            leaveRequest.setCreatedBy(user);

            // Thiết lập trạng thái ban đầu (1: Pending)
            leaveRequest.setStatus(1);

            // Lưu vào database
            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
            boolean success = leaveRequestDAO.addLeaveRequest(leaveRequest);

            if (success) {
                // Redirect đến trang danh sách leave request với thông báo thành công
                response.sendRedirect(request.getContextPath() + "/leave/list?message=created");
            } else {
                // Quay lại form với thông báo lỗi
                request.setAttribute("error", "Failed to create leave request. Please try again.");
                doGet(request, response);
            }

        } catch (Exception e) {
            // Xử lý lỗi
            request.setAttribute("error", "Error: " + e.getMessage());
            doGet(request, response);
        }
    }
}
