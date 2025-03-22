package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    // Get all leave requests with search, filter and pagination
    public List<LeaveRequest> getAllLeaveRequests(String title, Date fromDate, Date toDate,
            String createdBy, String ownerName, Integer status, int page, int pageSize) {
        List<LeaveRequest> requests = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT lr.* FROM LeaveRequests lr ");

        // Join with User table for createdBy search
        if (createdBy != null && !createdBy.trim().isEmpty()) {
            sqlBuilder.append("JOIN Users u ON lr.createdBy = u.username ");
        }

        // Join with Staff table for owner search
        if (ownerName != null && !ownerName.trim().isEmpty()) {
            sqlBuilder.append("JOIN Staff s ON lr.ownerId = s.id ");
            sqlBuilder.append("JOIN Users u2 ON s.userId = u2.id ");
        }

        sqlBuilder.append("WHERE 1=1 ");

        // Add search conditions
        if (title != null && !title.trim().isEmpty()) {
            sqlBuilder.append("AND lr.title LIKE ? ");
        }

        if (fromDate != null) {
            sqlBuilder.append("AND lr.createdAt >= ? ");
        }

        if (toDate != null) {
            sqlBuilder.append("AND lr.createdAt <= ? ");
        }

        if (createdBy != null && !createdBy.trim().isEmpty()) {
            sqlBuilder.append("AND (u.username LIKE ? OR u.fullName LIKE ?) ");
        }

        if (ownerName != null && !ownerName.trim().isEmpty()) {
            sqlBuilder.append("AND (u2.username LIKE ? OR u2.fullName LIKE ?) ");
        }

        if (status != null) {
            sqlBuilder.append("AND lr.status = ? ");
        }

        // Add order by and pagination
        sqlBuilder.append("ORDER BY lr.createdAt DESC ");
        sqlBuilder.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;

            // Set parameters for search conditions
            if (title != null && !title.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }

            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }

            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }

            if (createdBy != null && !createdBy.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + createdBy + "%");
                ps.setString(paramIndex++, "%" + createdBy + "%");
            }

            if (ownerName != null && !ownerName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + ownerName + "%");
                ps.setString(paramIndex++, "%" + ownerName + "%");
            }

            if (status != null) {
                ps.setInt(paramIndex++, status);
            }

            // Set pagination parameters
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex, pageSize);

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

    // Count total leave requests for pagination
    public int countAllLeaveRequests(String title, Date fromDate, Date toDate,
            String createdBy, String ownerName, Integer status) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM LeaveRequests lr ");

        // Join with User table for createdBy search
        if (createdBy != null && !createdBy.trim().isEmpty()) {
            sqlBuilder.append("JOIN Users u ON lr.createdBy = u.username ");
        }

        // Join with Staff table for owner search
        if (ownerName != null && !ownerName.trim().isEmpty()) {
            sqlBuilder.append("JOIN Staff s ON lr.ownerId = s.id ");
            sqlBuilder.append("JOIN Users u2 ON s.userId = u2.id ");
        }

        sqlBuilder.append("WHERE 1=1 ");

        // Add search conditions
        if (title != null && !title.trim().isEmpty()) {
            sqlBuilder.append("AND lr.title LIKE ? ");
        }

        if (fromDate != null) {
            sqlBuilder.append("AND lr.createdAt >= ? ");
        }

        if (toDate != null) {
            sqlBuilder.append("AND lr.createdAt <= ? ");
        }

        if (createdBy != null && !createdBy.trim().isEmpty()) {
            sqlBuilder.append("AND (u.username LIKE ? OR u.fullName LIKE ?) ");
        }

        if (ownerName != null && !ownerName.trim().isEmpty()) {
            sqlBuilder.append("AND (u2.username LIKE ? OR u2.fullName LIKE ?) ");
        }

        if (status != null) {
            sqlBuilder.append("AND lr.status = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;

            // Set parameters for search conditions
            if (title != null && !title.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }

            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }

            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }

            if (createdBy != null && !createdBy.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + createdBy + "%");
                ps.setString(paramIndex++, "%" + createdBy + "%");
            }

            if (ownerName != null && !ownerName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + ownerName + "%");
                ps.setString(paramIndex++, "%" + ownerName + "%");
            }

            if (status != null) {
                ps.setInt(paramIndex, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
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

    // Get all leave requests by owner ID with search, filter and pagination
    public List<LeaveRequest> getLeaveRequestsByOwner(int ownerId, String title, Date fromDate,
            Date toDate, Integer status, int page, int pageSize) {
        List<LeaveRequest> requests = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM LeaveRequests WHERE ownerId = ? ");

        // Add search conditions
        if (title != null && !title.trim().isEmpty()) {
            sqlBuilder.append("AND title LIKE ? ");
        }

        if (fromDate != null) {
            sqlBuilder.append("AND createdAt >= ? ");
        }

        if (toDate != null) {
            sqlBuilder.append("AND createdAt <= ? ");
        }

        if (status != null) {
            sqlBuilder.append("AND status = ? ");
        }

        // Add order by and pagination
        sqlBuilder.append("ORDER BY createdAt DESC ");
        sqlBuilder.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;

            // Set owner ID parameter
            ps.setInt(paramIndex++, ownerId);

            // Set parameters for search conditions
            if (title != null && !title.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }

            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }

            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }

            if (status != null) {
                ps.setInt(paramIndex++, status);
            }

            // Set pagination parameters
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex, pageSize);

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

    // Count total leave requests by owner for pagination
    public int countLeaveRequestsByOwner(int ownerId, String title, Date fromDate,
            Date toDate, Integer status) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM LeaveRequests WHERE ownerId = ? ");

        // Add search conditions
        if (title != null && !title.trim().isEmpty()) {
            sqlBuilder.append("AND title LIKE ? ");
        }

        if (fromDate != null) {
            sqlBuilder.append("AND createdAt >= ? ");
        }

        if (toDate != null) {
            sqlBuilder.append("AND createdAt <= ? ");
        }

        if (status != null) {
            sqlBuilder.append("AND status = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;

            // Set owner ID parameter
            ps.setInt(paramIndex++, ownerId);

            // Set parameters for search conditions
            if (title != null && !title.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + title + "%");
            }

            if (fromDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fromDate.getTime()));
            }

            if (toDate != null) {
                ps.setDate(paramIndex++, new java.sql.Date(toDate.getTime()));
            }

            if (status != null) {
                ps.setInt(paramIndex, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
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

        // Set owner (Staff) and createdBy (User)
        UserDAO userDAO = new UserDAO();
        request.setOwner(userDAO.findStaffById(rs.getInt("ownerId")));
        request.setCreatedBy(userDAO.findUserByUsername(rs.getString("createdBy")));

        request.setCreateddate(rs.getTimestamp("createdAt"));
        return request;
    }
}
