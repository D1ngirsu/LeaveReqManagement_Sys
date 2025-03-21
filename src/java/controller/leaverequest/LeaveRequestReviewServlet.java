package controller.leaverequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import dao.LeaveRequestDAO;
import model.LeaveRequest;
import model.Staff;

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
        if (currentUser.getRoleName().equals("Nhân viên")) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
            return;
        }

        try {
            // Lấy các tham số tìm kiếm và phân trang từ request
            String title = request.getParameter("title");
            String createdBy = request.getParameter("createdBy");
            String ownerName = request.getParameter("ownerName");
            String statusParam = request.getParameter("status");
            String fromDateStr = request.getParameter("fromDate");
            String toDateStr = request.getParameter("toDate");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");
            
            // Xử lý tham số status
            Integer status = null;
            if (statusParam != null && !statusParam.isEmpty()) {
                status = Integer.parseInt(statusParam);
            }
            
            // Xử lý tham số ngày
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date fromDate = null;
            Date toDate = null;
            
            if (fromDateStr != null && !fromDateStr.isEmpty()) {
                fromDate = new Date(dateFormat.parse(fromDateStr).getTime());
            }
            
            if (toDateStr != null && !toDateStr.isEmpty()) {
                toDate = new Date(dateFormat.parse(toDateStr).getTime());
            }
            
            // Xử lý tham số phân trang
            int page = 1;
            int pageSize = 10;
            
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }
            
            if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
            
            // Lấy danh sách leave request dựa trên vai trò
            LeaveRequestDAO leaveRequestDAO = new LeaveRequestDAO();
            List<LeaveRequest> leaveRequests;
            int totalRecords = 0;

            if (currentUser.getRoleName().equals("Division Leader")) {
                // Lấy tất cả request để lọc theo quyền Division Leader
                List<LeaveRequest> allRequests = leaveRequestDAO.getAllLeaveRequests(
                    title, fromDate, toDate, createdBy, ownerName, status, 1, Integer.MAX_VALUE);
                
                // Lọc theo quyền của Division Leader
                List<LeaveRequest> filteredRequests = allRequests.stream()
                    .filter(lr -> 
                        // Requests của Trưởng nhóm và Nhân viên cùng division
                        ((lr.getOwner().getRoleName().equals("Trưởng nhóm") || 
                          lr.getOwner().getRoleName().equals("Nhân viên"))
                            && lr.getOwner().getDivisionId() == currentUser.getDivisionId()) ||
                        // Requests của chính Division Leader
                        lr.getOwner().getId() == currentUser.getId())
                    .collect(Collectors.toList());
                
                // Đếm tổng số bản ghi sau khi lọc
                totalRecords = filteredRequests.size();
                
                // Áp dụng phân trang thủ công sau khi đã lọc
                int fromIndex = (page - 1) * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, filteredRequests.size());
                
                if (fromIndex < filteredRequests.size()) {
                    leaveRequests = filteredRequests.subList(fromIndex, toIndex);
                } else {
                    leaveRequests = List.of();
                }
                
            } else if (currentUser.getRoleName().equals("Trưởng nhóm")) {
                // Lấy tất cả request để lọc theo quyền Trưởng nhóm
                List<LeaveRequest> allRequests = leaveRequestDAO.getAllLeaveRequests(
                    title, fromDate, toDate, createdBy, ownerName, status, 1, Integer.MAX_VALUE);
                
                // Lọc theo quyền của Trưởng nhóm
                List<LeaveRequest> filteredRequests = allRequests.stream()
                    .filter(lr -> {
                        // Nếu owner là Nhân viên và cùng division
                        boolean sameRole = lr.getOwner().getRoleName().equals("Nhân viên");
                        boolean sameDivision = lr.getOwner().getDivisionId() == currentUser.getDivisionId();
                        
                        // Kiểm tra nếu cả hai có cùng group (nếu group của Trưởng nhóm không null)
                        boolean sameGroup = true; // Mặc định là true để không ảnh hưởng nếu group null
                        if (currentUser.getGroup() != null) {
                            // Nếu Trưởng nhóm có group, thì nhân viên phải cùng group
                            sameGroup = currentUser.getGroup().equals(lr.getOwner().getGroup());
                        }
                        
                        return sameRole && sameDivision && sameGroup;
                    })
                    .collect(Collectors.toList());
                
                // Đếm tổng số bản ghi sau khi lọc
                totalRecords = filteredRequests.size();
                
                // Áp dụng phân trang thủ công sau khi đã lọc
                int fromIndex = (page - 1) * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, filteredRequests.size());
                
                if (fromIndex < filteredRequests.size()) {
                    leaveRequests = filteredRequests.subList(fromIndex, toIndex);
                } else {
                    leaveRequests = List.of();
                }
                
            } else {
                // Trường hợp không được phép
                leaveRequests = List.of();
                totalRecords = 0;
            }

            // Tính toán phân trang
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            
            // Truyền dữ liệu xuống view
            request.setAttribute("leaveRequests", leaveRequests);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            
            // Giữ lại các tham số tìm kiếm để hiển thị trên form
            request.setAttribute("title", title);
            request.setAttribute("createdBy", createdBy);
            request.setAttribute("ownerName", ownerName);
            request.setAttribute("status", status);
            request.setAttribute("fromDate", fromDateStr);
            request.setAttribute("toDate", toDateStr);
            
            request.getRequestDispatcher("/view/leaverequest/review.jsp").forward(request, response);
            
        } catch (ParseException e) {
            request.setAttribute("error", "Error parsing date: " + e.getMessage());
            request.getRequestDispatcher("/view/leaverequest/review.jsp").forward(request, response);
        }
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
        if (currentUser.getRoleName().equals("Nhân viên")) {
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
            if (currentUser.getRoleName().equals("Division Leader")) {
                // Division Leader có thể duyệt:
                // 1. Request của Trưởng nhóm và Nhân viên cùng division
                // 2. Request của chính mình
                if (!((leaveRequest.getOwner().getRoleName().equals("Trưởng nhóm") || 
                       leaveRequest.getOwner().getRoleName().equals("Nhân viên")) &&
                      leaveRequest.getOwner().getDivisionId() == currentUser.getDivisionId()) &&
                    leaveRequest.getOwner().getId() != currentUser.getId()) {
                    response.sendRedirect(request.getContextPath() + "/access-denied.jsp");
                    return;
                }
            } else if (currentUser.getRoleName().equals("Trưởng nhóm")) {
                // Trưởng nhóm chỉ duyệt request của Nhân viên cùng division và cùng group (nếu có)
                boolean sameRole = leaveRequest.getOwner().getRoleName().equals("Nhân viên");
                boolean sameDivision = leaveRequest.getOwner().getDivisionId() == currentUser.getDivisionId();
                
                boolean sameGroup = true; // Mặc định là true để không ảnh hưởng nếu group null
                if (currentUser.getGroup() != null) {
                    // Nếu Trưởng nhóm có group, thì nhân viên phải cùng group
                    sameGroup = currentUser.getGroup().equals(leaveRequest.getOwner().getGroup());
                }
                
                if (!(sameRole && sameDivision && sameGroup)) {
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
                // Giữ lại các tham số tìm kiếm khi chuyển hướng
                String queryString = request.getQueryString();
                if (queryString != null && !queryString.contains("requestId") && !queryString.contains("status")) {
                    response.sendRedirect(request.getContextPath() + "/leave/review?" + queryString + "&message=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/leave/review?message=updated");
                }
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