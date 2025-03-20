package dao;

import java.sql.*;
import java.util.*;
import model.Staff;
import model.User;

public class UserDAO extends DBContext {

    public UserDAO() {
        super();
    }

    // Authenticate method
    public User authenticate(String username, String password) {
        return findStaffByCredentials(username, password);
    }

    private Staff findStaffByCredentials(String username, String password) {
        String sql = "SELECT u.id, u.username, u.password, u.email, u.fullName, "
                + "s.divisionId, d.divisionName, s.roleId, r.roleName, s.[group] "
                + "FROM Users u "
                + "JOIN Staff s ON u.id = s.id "
                + "JOIN Division d ON s.divisionId = d.divisionId "
                + "JOIN Role r ON s.roleId = r.roleId "
                + "WHERE u.username = ? AND u.password = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);  // Cần mã hóa mật khẩu trong ứng dụng thực tế

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Add a new staff member
    public boolean addStaff(Staff staff) {
        String sqlUser = "INSERT INTO Users (username, password, email, fullName) VALUES (?, ?, ?, ?)";
        String sqlStaff = "INSERT INTO Staff (id, divisionId, roleId, [group]) VALUES (?, ?, ?, ?)";

        try (PreparedStatement psUser = connection.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
            psUser.setString(1, staff.getUsername());
            psUser.setString(2, staff.getPassword());  // Phải mã hóa trong thực tế
            psUser.setString(3, staff.getEmail());
            psUser.setString(4, staff.getFullName());

            int affectedRows = psUser.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = psUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        staff.setId(userId);

                        try (PreparedStatement psStaff = connection.prepareStatement(sqlStaff)) {
                            psStaff.setInt(1, userId);
                            psStaff.setInt(2, staff.getDivisionId());
                            psStaff.setInt(3, staff.getRoleId());
                            
                            // Xử lý trường group có thể null
                            if (staff.getGroup() != null) {
                                psStaff.setString(4, staff.getGroup());
                            } else {
                                psStaff.setNull(4, Types.NVARCHAR);
                            }
                            
                            return psStaff.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Update staff information
    public boolean updateStaff(Staff staff) {
        String sqlUser = "UPDATE Users SET username = ?, fullName = ?, email = ? WHERE id = ?";
        String sqlStaff = "UPDATE Staff SET divisionId = ?, roleId = ?, [group] = ? WHERE id = ?";

        try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
            psUser.setString(1, staff.getUsername());
            psUser.setString(2, staff.getFullName());
            psUser.setString(3, staff.getEmail());
            psUser.setInt(4, staff.getId());
            int updatedUser = psUser.executeUpdate();

            try (PreparedStatement psStaff = connection.prepareStatement(sqlStaff)) {
                psStaff.setInt(1, staff.getDivisionId());
                psStaff.setInt(2, staff.getRoleId());
                
                // Xử lý trường group có thể null
                if (staff.getGroup() != null) {
                    psStaff.setString(3, staff.getGroup());
                } else {
                    psStaff.setNull(3, Types.NVARCHAR);
                }
                
                psStaff.setInt(4, staff.getId());
                int updatedStaff = psStaff.executeUpdate();
                return updatedUser > 0 && updatedStaff > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Delete a staff member
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM Users WHERE id = ?"; // Staff sẽ bị xóa tự động nhờ ON DELETE CASCADE

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Get all staff members
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.fullName, u.email, "
                + "s.divisionId, d.divisionName, s.roleId, r.roleName, s.[group] "
                + "FROM Users u "
                + "JOIN Staff s ON u.id = s.id "
                + "JOIN Division d ON s.divisionId = d.divisionId "
                + "JOIN Role r ON s.roleId = r.roleId";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return staffList;
    }

    // Find staff by ID
    public Staff findStaffById(int id) {
        String sql = "SELECT u.id, u.username, u.fullName, u.email, "
                + "s.divisionId, d.divisionName, s.roleId, r.roleName, s.[group] "
                + "FROM Users u "
                + "JOIN Staff s ON u.id = s.id "
                + "JOIN Division d ON s.divisionId = d.divisionId "
                + "JOIN Role r ON s.roleId = r.roleId "
                + "WHERE u.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Staff findUserByUsername(String username) {
        String sql = "SELECT u.id, u.username, u.email, u.fullName, "
                + "s.divisionId, d.divisionName, s.roleId, r.roleName, s.[group] "
                + "FROM Users u "
                + "LEFT JOIN Staff s ON u.id = s.id "
                + "LEFT JOIN Division d ON s.divisionId = d.divisionId "
                + "LEFT JOIN Role r ON s.roleId = r.roleId "
                + "WHERE u.username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Helper method to map ResultSet to Staff object
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getInt("id"));
        staff.setUsername(rs.getString("username"));
        staff.setFullName(rs.getString("fullName"));
        staff.setEmail(rs.getString("email"));
        
        staff.setDivisionId(rs.getInt("divisionId"));
        staff.setDivisionName(rs.getString("divisionName"));
        staff.setRoleId(rs.getInt("roleId"));
        staff.setRoleName(rs.getString("roleName"));
        
        // Xử lý trường group có thể null
        String group = rs.getString("group");
        if (rs.wasNull()) {
            staff.setGroup(null);
        } else {
            staff.setGroup(group);
        }
        
        return staff;
    }
    
    // Phương thức tiện ích để lấy tất cả các Division
    public Map<Integer, String> getAllDivisions() {
        Map<Integer, String> divisions = new HashMap<>();
        String sql = "SELECT divisionId, divisionName FROM Division";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                divisions.put(rs.getInt("divisionId"), rs.getString("divisionName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return divisions;
    }
    
    // Phương thức tiện ích để lấy tất cả các Role
    public Map<Integer, String> getAllRoles() {
        Map<Integer, String> roles = new HashMap<>();
        String sql = "SELECT roleId, roleName FROM Role";
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.put(rs.getInt("roleId"), rs.getString("roleName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return roles;
    }
}