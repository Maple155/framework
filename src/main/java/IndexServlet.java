package servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;


public class IndexServlet  extends HttpServlet{
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    resp.setContentType("text/html");
    String path = req.getContextPath();

    PrintWriter out = resp.getWriter();
    
    out.println("<p><a href= '" + path + "/prevision' >Prevision</a></p>");
    out.println("<p><a href= '" + path + "/depense' >Depense</a></p>");
    out.println("<p><a href= '" + path + "/etat' >Etat</a></p>");

    }

}