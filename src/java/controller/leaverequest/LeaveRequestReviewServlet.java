package controller.leaverequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import dao.LeaveRequestDAO;
import dao.UserDAO;
import model.LeaveRequest;
import model.Staff;
import model.User;

@WebServlet(name = "LeaveRequestReviewServlet", urlPatterns = {"/leave/review"})
public class LeaveRequestReviewServlet extends HttpServlet {

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

        // Kiểm tra quyền truy cập
        if (currentUser.getRole().equals("Nhân viên")) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
            return;
        }

        // Lấy danh sách leave request dựa trên vai trò
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        List<LeaveRequest> leaveRequests;

        if (currentUser.getRole().equals("Division Leader")) {
            // Division Leader xem các request của:
            // 1. Trưởng nhóm và Nhân viên cùng division
            // 2. Của chính mình
            leaveRequests = leaveRequestDAO.getAllLeaveRequests().stream()
                .filter(lr -> 
                    // Requests của Trưởng nhóm và Nhân viên cùng division
                    ((lr.getOwner().getRole().equals("Trưởng nhóm") || lr.getOwner().getRole().equals("Nhân viên"))
                        && lr.getOwner().getDivision().equals(currentUser.getDivision())) ||
                    // Requests của chính Division Leader
                    lr.getOwner().getId() == currentUser.getId())
                .collect(Collectors.toList());
        } else if (currentUser.getRole().equals("Trưởng nhóm")) {
            // Trưởng nhóm chỉ xem request của Nhân viên cùng division
            leaveRequests = leaveRequestDAO.getAllLeaveRequests().stream()
                .filter(lr -> lr.getOwner().getRole().equals("Nhân viên") 
                        && lr.getOwner().getDivision().equals(currentUser.getDivision()))
                .collect(Collectors.toList());
        } else {
            // Trường hợp không được phép
            leaveRequests = List.of();
        }

        request.setAttribute("leaveRequests", leaveRequests);
        request.getRequestDispatcher("/view/leaverequest/review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        Staff currentUser = (Staff) session.getAttribute("staff");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra quyền truy cập
        if (currentUser.getRole().equals("Nhân viên")) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
            return;
        }

        try {
            // Lấy thông tin từ form
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            int status = Integer.parseInt(request.getParameter("status"));

            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
            LeaveRequest leaveRequest = leaveRequestDAO.findLeaveRequestById(requestId);

            // Kiểm tra quyền duyệt request
            if (currentUser.getRole().equals("Division Leader")) {
                // Division Leader có thể duyệt:
                // 1. Request của Trưởng nhóm và Nhân viên cùng division
                // 2. Request của chính mình
                if (!((leaveRequest.getOwner().getRole().equals("Trưởng nhóm") || 
                       leaveRequest.getOwner().getRole().equals("Nhân viên")) &&
                      leaveRequest.getOwner().getDivision().equals(currentUser.getDivision())) &&
                    leaveRequest.getOwner().getId() != currentUser.getId()) {
                    response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
                    return;
                }
            } else if (currentUser.getRole().equals("Trưởng nhóm")) {
                // Trưởng nhóm chỉ duyệt request của Nhân viên cùng division
                if (!leaveRequest.getOwner().getRole().equals("Nhân viên") ||
                    !leaveRequest.getOwner().getDivision().equals(currentUser.getDivision())) {
                    response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
                    return;
                }
            }

            // Kiểm tra trạng thái hiện tại và không cho phép đảo ngược
            if (leaveRequest.getStatus() == 0 || leaveRequest.getStatus() == 2) {
                request.setAttribute("error", "This request cannot be modified further.");
                doGet(request, response);
                return;
            }

            // Cập nhật trạng thái
            leaveRequest.setStatus(status);

            boolean success = leaveRequestDAO.updateLeaveRequest(leaveRequest);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/leave/review?message=updated");
            } else {
                request.setAttribute("error", "Failed to update leave request.");
                doGet(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            doGet(request, response);
        }
    }
}