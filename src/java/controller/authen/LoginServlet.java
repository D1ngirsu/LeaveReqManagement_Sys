package controller.authen;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import model.*;
import dao.UserDAO;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password"); // Should be hashed
        
        // Create DAO instance
        UserDAO userDAO = new UserDAO();
        
        // Authenticate user
        User user = userDAO.authenticate(username, password);
        
        if (user != null && user instanceof Staff) {
            // Create session
            HttpSession session = request.getSession(true);
            
            // Store staff in session
            session.setAttribute("staff", user);
            
            // Redirect to home page or dashboard
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            // Authentication failed
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}