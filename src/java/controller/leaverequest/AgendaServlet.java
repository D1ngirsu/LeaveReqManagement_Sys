package controller.leaverequest;

import dao.LeaveRequestDAO;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LeaveRequest;
import model.Staff;
import dao.UserDAO;

@WebServlet(name = "AgendaServlet", urlPatterns = {"/agenda"})
public class AgendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get start and end date parameters, or use defaults (current month)
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate;
            Date endDate;

            if (startDateParam == null || startDateParam.isEmpty()) {
                // Default to beginning of current month
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
            } else {
                startDate = sdf.parse(startDateParam);
            }

            if (endDateParam == null || endDateParam.isEmpty()) {
                // Default to start date + 8 days (to show 9 days total)
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.DAY_OF_MONTH, 8); // 9 days total (from 1/1 to 9/1)
                endDate = cal.getTime();
            } else {
                endDate = sdf.parse(endDateParam);
            }

            // Get all staff members
            UserDAO userDAO = new UserDAO();
            List<Staff> staffList = userDAO.getAllStaff();

            // Get leave requests for the date range
            LeaveRequestDAO leaveDAO = new LeaveRequestDAO();
            List<LeaveRequest> allLeaveRequests = new ArrayList<>();

            // Convert java.util.Date to java.sql.Date for DAO methods
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            // Collect leave requests for all staff
            for (Staff staff : staffList) {
                // For better performance, you might want to modify the DAO to get all leaves in one query
                allLeaveRequests = leaveDAO.findLeaveRequestsByDateRange(null, sqlStartDate, sqlEndDate);
            }

            System.out.println("Total leave requests found: " + allLeaveRequests.size());
            for (LeaveRequest leave : allLeaveRequests) {
                System.out.println("Leave ID: " + leave.getRid()
                        + ", Owner ID: " + (leave.getOwner() != null ? leave.getOwner().getId() : "null")
                        + ", Start: " + leave.getStartDate()
                        + ", End: " + leave.getEndDate()
                        + ", Status: " + leave.getStatus());
            }

            // Create a map of staff ID to leave status by date
            Map<Integer, Map<String, Integer>> staffLeaveStatus = new HashMap<>();

            // Initialize the map for each staff member
            for (Staff staff : staffList) {
                Map<String, Integer> dateStatusMap = new HashMap<>();
                staffLeaveStatus.put(staff.getId(), dateStatusMap);
            }

            // Populate leave statuses
            // In AgendaServlet.java, check this section:
            for (LeaveRequest leave : allLeaveRequests) {
                if (leave.getStartDate() == null || leave.getEndDate() == null) {
                    continue;
                }

                // Get all dates between start and end date
                Calendar currentDate = Calendar.getInstance();
                currentDate.setTime(leave.getStartDate());

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(leave.getEndDate());
                endCal.add(Calendar.DAY_OF_MONTH, 1); // Include end date

                while (currentDate.before(endCal)) {
                    Date date = currentDate.getTime();
                    String dateStr = sdf.format(date);

                    // Add to the map for this staff member
                    Map<String, Integer> dateStatusMap = staffLeaveStatus.get(leave.getOwner().getId());
                    dateStatusMap.put(dateStr, leave.getStatus());

                    currentDate.add(Calendar.DAY_OF_MONTH, 1);
                }
            }

            // Generate list of dates for display
            List<Date> displayDates = new ArrayList<>();
            Calendar currentDate = Calendar.getInstance();
            currentDate.setTime(startDate);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.add(Calendar.DAY_OF_MONTH, 1); // To include the end date

            while (currentDate.before(endCal)) {
                displayDates.add(currentDate.getTime());
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Set attributes for JSP
            request.setAttribute("staffList", staffList);
            request.setAttribute("displayDates", displayDates);
            request.setAttribute("staffLeaveStatus", staffLeaveStatus);

            // Format for JSP display
            request.setAttribute("dateFormat", new SimpleDateFormat("d/M"));

        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Invalid date format: " + e.getMessage());
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Forward to JSP
        request.getRequestDispatcher("/view/leaverequest/agenda.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
