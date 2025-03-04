package filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * Filter for authentication in the Leave Request Management System
 */
public class Authenticate implements Filter {
    
    // URLs that don't require authentication
    private static final List<String> ALLOWED_URLS = Arrays.asList(
        "/login", "/login.jsp", "/forgot-password.jsp",
        "/register.jsp", "/verify.jsp", "/new-password.jsp", "/reset-verify.jsp",
        "/register", "/logingg", "/reset-password",
        "/assets/", "/layout/"
    );
    
    private static final boolean debug = true;
    private FilterConfig filterConfig = null;
    
    public Authenticate() {
    }    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();
        HttpSession session = req.getSession(false);

        // Nếu URL thuộc danh sách được phép -> cho phép truy cập
        if (isAllowed(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Nếu user đã đăng nhập (staff hoặc nhân viên) -> cho phép truy cập
        if (session != null && session.getAttribute("staff") != null) {
            chain.doFilter(request, response);
            return;
        }

        // Nếu chưa đăng nhập và không phải đang ở /login -> Redirect về trang đăng nhập
        if (!requestURI.endsWith("/login") && !requestURI.endsWith("/login.jsp")) {
            res.sendRedirect(req.getContextPath() + "/login");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isAllowed(String requestURI) {
        for (String url : ALLOWED_URLS) {
            if (requestURI.contains(url)) {
                return true;
            }
        }
        return requestURI.startsWith("/assets/"); // Cho phép truy cập tài nguyên tĩnh
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("Authenticate:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("Authenticate()");
        }
        StringBuffer sb = new StringBuffer("Authenticate(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
}