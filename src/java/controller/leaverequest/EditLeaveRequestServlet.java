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

@WebServlet(name = "EditLeaveRequestServlet", urlPatterns = {"/leave/edit"})
public class EditLeaveRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check login
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("staff");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get leave request ID
        String ridParam = request.getParameter("rid");
        if (ridParam == null || ridParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/leave/list");
            return;
        }

        int rid = Integer.parseInt(ridParam);

        // Fetch the leave request
        LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
        LeaveRequest leaveRequest = leaveRequestDAO.findLeaveRequestById(rid);

        // Validate request ownership and status
        if (leaveRequest == null || 
            leaveRequest.getOwner().getId() != user.getId() || 
            leaveRequest.getStatus() != 1) {
            response.sendRedirect(request.getContextPath() + "/leave/list");
            return;
        }

        // Get list of employees for potential employee selection
        UserDAO userDAO = new UserDAO();
        List<Staff> employees = userDAO.getAllStaff();
        request.setAttribute("employees", employees);

        // Set leave request for the form
        request.setAttribute("leaveRequest", leaveRequest);

        // Forward to edit page
        request.getRequestDispatcher("/view/leaverequest/edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check login
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("staff");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get form data
            int rid = Integer.parseInt(request.getParameter("rid"));
            String title = request.getParameter("title");
            String reason = request.getParameter("reason");
            Date fromDate = Date.valueOf(request.getParameter("from"));
            Date toDate = Date.valueOf(request.getParameter("to"));

            // Fetch existing leave request to validate
            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
            LeaveRequest existingRequest = leaveRequestDAO.findLeaveRequestById(rid);

            // Validate request ownership and status
            if (existingRequest == null || 
                existingRequest.getOwner().getId() != user.getId() || 
                existingRequest.getStatus() != 1) {
                response.sendRedirect(request.getContextPath() + "/leave/list");
                return;
            }

            // Update the leave request
            existingRequest.setTitle(title);
            existingRequest.setReason(reason);
            existingRequest.setStartDate(fromDate);
            existingRequest.setEndDate(toDate);

            // Save the updated request
            boolean success = leaveRequestDAO.updateLeaveRequest(existingRequest);

            if (success) {
                // Redirect with success message
                response.sendRedirect(request.getContextPath() + "/leave/list?message=updated");
            } else {
                // If update fails
                request.setAttribute("error", "Failed to update leave request. Please try again.");
                request.setAttribute("leaveRequest", existingRequest);
                request.getRequestDispatcher("/view/leaverequest/edit.jsp").forward(request, response);
            }

        } catch (Exception e) {
            // Handle errors
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/leaverequest/edit.jsp").forward(request, response);
        }
    }
}