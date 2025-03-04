package controller.leaverequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

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

        // Lấy danh sách leave request của người dùng hiện tại
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        List<LeaveRequest> leaveRequests = leaveRequestDAO.getLeaveRequestsByOwner(currentUser.getId());

        // Thêm danh sách request vào request scope
        request.setAttribute("leaveRequests", leaveRequests);

        // Chuyển đến trang JSP hiển thị danh sách
        request.getRequestDispatcher("/view/leaverequest/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu cần thêm chức năng post trong tương lai
        doGet(request, response);
    }
}