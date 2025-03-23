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
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.LeaveRequest;
import model.Staff;
import dao.UserDAO;

@WebServlet(name = "AgendaServlet", urlPatterns = {"/agenda"})
public class AgendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get current user from session
            HttpSession session = request.getSession();
            Staff currentUser = (Staff) session.getAttribute("staff");

            // Get month/year parameters or navigation action
            String monthParam = request.getParameter("month");
            String yearParam = request.getParameter("year");
            String navAction = request.getParameter("nav");
            
            // Get start and end date parameters
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate;
            Date endDate;
            
            Calendar current = Calendar.getInstance();
            int year = current.get(Calendar.YEAR);
            int month = current.get(Calendar.MONTH);
            
            boolean customDateRange = false;
            
            if (startDateParam != null && !startDateParam.isEmpty() && 
                endDateParam != null && !endDateParam.isEmpty()) {
                // Custom date range specified
                startDate = sdf.parse(startDateParam);
                endDate = sdf.parse(endDateParam);
                
                // Ensure date range doesn't exceed 30 days
                Calendar tempEnd = Calendar.getInstance();
                tempEnd.setTime(startDate);
                tempEnd.add(Calendar.DAY_OF_MONTH, 30);
                
                if (endDate.after(tempEnd.getTime())) {
                    endDate = tempEnd.getTime();
                    request.setAttribute("warningMessage", "Date range limited to 30 days maximum.");
                }
                
                customDateRange = true;
                
            } else if (navAction != null) {
                // Month navigation
                if (monthParam != null && !monthParam.isEmpty() && 
                    yearParam != null && !yearParam.isEmpty()) {
                    month = Integer.parseInt(monthParam);
                    year = Integer.parseInt(yearParam);
                }
                
                if ("prev".equals(navAction)) {
                    if (month == 0) {
                        month = 11;
                        year--;
                    } else {
                        month--;
                    }
                } else if ("next".equals(navAction)) {
                    if (month == 11) {
                        month = 0;
                        year++;
                    } else {
                        month++;
                    }
                }
                
                // Set to first day of the month
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1);
                startDate = cal.getTime();
                
                // Set to last day of the month
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endDate = cal.getTime();
                
            } else if (monthParam != null && !monthParam.isEmpty() && 
                       yearParam != null && !yearParam.isEmpty()) {
                // Month and year parameters specified
                month = Integer.parseInt(monthParam);
                year = Integer.parseInt(yearParam);
                
                // Set to first day of the month
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1);
                startDate = cal.getTime();
                
                // Set to last day of the month
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endDate = cal.getTime();
                
            } else {
                // Default to current month
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endDate = cal.getTime();
            }

            // Get all staff members
            UserDAO userDAO = new UserDAO();
            List<Staff> staffList = userDAO.getAllStaff();
            
            // Filter staff list only if user is a Division Leader
            if (currentUser != null && currentUser.getRoleName().equals("Division Leader")) {
                // Division Leader can see staff in the same division
                int divisionId = currentUser.getDivisionId();
                staffList = staffList.stream()
                    .filter(staff -> staff.getDivisionId() == divisionId)
                    .collect(Collectors.toList());
            }

            // Get leave requests for the date range
            LeaveRequestDAO leaveDAO = new LeaveRequestDAO();
            List<LeaveRequest> allLeaveRequests = new ArrayList<>();

            // Convert java.util.Date to java.sql.Date for DAO methods
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            // Get all leave requests for the date range
            allLeaveRequests = leaveDAO.findLeaveRequestsByDateRange(null, sqlStartDate, sqlEndDate);
            
            // Filter leave requests if user is a Division Leader
            if (currentUser != null && currentUser.getRoleName().equals("Division Leader")) {
                int divisionId = currentUser.getDivisionId();
                allLeaveRequests = allLeaveRequests.stream()
                    .filter(leave -> leave.getOwner() != null && leave.getOwner().getDivisionId() == divisionId)
                    .collect(Collectors.toList());
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
            for (LeaveRequest leave : allLeaveRequests) {
                if (leave.getStartDate() == null || leave.getEndDate() == null || leave.getOwner() == null) {
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
                    if (dateStatusMap != null) {
                        dateStatusMap.put(dateStr, leave.getStatus());
                    }

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

            // Get today's date for highlighting
            Date today = new Date();
            SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayStr = todayFormat.format(today);

            // Set attributes for JSP
            request.setAttribute("staffList", staffList);
            request.setAttribute("displayDates", displayDates);
            request.setAttribute("staffLeaveStatus", staffLeaveStatus);
            request.setAttribute("today", todayStr);
            request.setAttribute("currentMonth", month);
            request.setAttribute("currentYear", year);
            request.setAttribute("customDateRange", customDateRange);
            request.setAttribute("startDate", sdf.format(startDate));
            request.setAttribute("endDate", sdf.format(endDate));
            
            // Format for display
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
            request.setAttribute("monthYearDisplay", monthFormat.format(startDate));
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