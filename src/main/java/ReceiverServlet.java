package servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class ReceiverServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        String name = (String) session.getAttribute("nom");
        if (name != null) {
            out.println(name);
        } else {
            out.println("pas de nom !!!");
        }
    }

}
