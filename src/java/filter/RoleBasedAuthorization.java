// RoleBasedAuthorization.java
package filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Staff;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RoleBasedAuthorization implements Filter {
    
    // Map of URL patterns to allowed roles
    private static final Map<String, List<String>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        // Initialize with URL patterns and allowed roles
        //ROLE_PERMISSIONS.put("/admin/", Arrays.asList("Division Leader"));
        //ROLE_PERMISSIONS.put("/manager/", Arrays.asList("Division Leader", "Trưởng nhóm"));
        ROLE_PERMISSIONS.put("/leave/create", Arrays.asList("Division Leader", "Trưởng nhóm", "Nhân viên"));
        ROLE_PERMISSIONS.put("/leave/review", Arrays.asList("Division Leader", "Trưởng nhóm"));
        ROLE_PERMISSIONS.put("/leave/list", Arrays.asList("Division Leader", "Trưởng nhóm", "Nhân viên"));
        ROLE_PERMISSIONS.put("/leave/edit", Arrays.asList("Division Leader", "Trưởng nhóm", "Nhân viên"));
        // Add more URL pattern to role mappings
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();
        HttpSession session = req.getSession(false);
        
        // If no session or not a staff, pass to next filter (may be handled by basic auth filter)
        if (session == null || session.getAttribute("staff") == null) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if this is a protected path requiring specific roles
        for (Map.Entry<String, List<String>> entry : ROLE_PERMISSIONS.entrySet()) {
            if (requestURI.contains(entry.getKey())) {
                // This is a protected path, check user's role
                Staff staff = (Staff) session.getAttribute("staff");
                if (!entry.getValue().contains(staff.getRoleName())) {
                    // User doesn't have permission
                    res.sendRedirect(req.getContextPath() + "/access-denied.jsp");
                    return;
                }
                break;
            }
        }
        
        // Either path doesn't require special role or user has correct role
        chain.doFilter(request, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void destroy() {
    }
}