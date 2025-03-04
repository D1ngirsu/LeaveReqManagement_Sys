package dao;

import java.sql.*;
import java.util.*;
import model.LeaveRequest;

public class LeaveRequestDAO extends DBContext {

    public LeaveRequestDAO() {
        super();
    }

    // Add a new leave request
    public boolean addLeaveRequest(LeaveRequest request) {
        String sql = "INSERT INTO LeaveRequests (title, reason, startDate, endDate, status, createdBy, ownerId, createdAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, request.getTitle());
            ps.setString(2, request.getReason());
            ps.setDate(3, new java.sql.Date(request.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(request.getEndDate().getTime()));
            ps.setInt(5, request.getStatus());
            ps.setString(6, request.getCreatedBy().getUsername());

            // Add null check before accessing owner
            if (request.getOwner() == null) {
                throw new SQLException("Owner cannot be null");
            }
            ps.setInt(7, request.getOwner().getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setRid(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Get all leave requests
    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM LeaveRequests";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapResultSetToLeaveRequest(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return requests;
    }

    // Find leave request by ID
    public LeaveRequest findLeaveRequestById(int rid) {
        String sql = "SELECT * FROM LeaveRequests WHERE rid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLeaveRequest(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Update a leave request
    public boolean updateLeaveRequest(LeaveRequest request) {
        String sql = "UPDATE LeaveRequests SET title = ?, reason = ?, startDate = ?, endDate = ?, "
                + "status = ?, ownerId = ? WHERE rid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, request.getTitle());
            ps.setString(2, request.getReason());
            ps.setDate(3, new java.sql.Date(request.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(request.getEndDate().getTime()));
            ps.setInt(5, request.getStatus());
            ps.setInt(6, request.getOwner().getId());
            ps.setInt(7, request.getRid());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Delete a leave request
    public boolean deleteLeaveRequest(int rid) {
        String sql = "DELETE FROM LeaveRequests WHERE rid = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, rid);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Get all leave requests by owner ID (Staff ID)
    public List<LeaveRequest> getLeaveRequestsByOwner(int ownerId) {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM LeaveRequests WHERE ownerId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToLeaveRequest(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return requests;
    }

    // Helper method to map ResultSet to LeaveRequest object
    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest request = new LeaveRequest();
        request.setRid(rs.getInt("rid"));
        request.setTitle(rs.getString("title"));
        request.setReason(rs.getString("reason"));
        request.setStartDate(rs.getDate("startDate"));
        request.setEndDate(rs.getDate("endDate"));
        request.setStatus(rs.getInt("status"));

        // Set owner (Staff) and createdBy (User
        UserDAO userDAO = new UserDAO();
        request.setOwner(userDAO.findStaffById(rs.getInt("ownerId")));
        request.setCreatedBy(userDAO.findUserByUsername(rs.getString("createdBy")));

        request.setCreateddate(rs.getTimestamp("createdAt"));
        return request;
    }
}
