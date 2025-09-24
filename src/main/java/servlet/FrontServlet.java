package servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class FrontServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        var out = response.getWriter();

        // Différentes infos sur l’URL
        String requestURL = request.getRequestURL().toString(); // l'URL complète
        String requestURI = request.getRequestURI();            // partie après le host
        String contextPath = request.getContextPath();          // le contexte de l'app
        String servletPath = request.getServletPath();          // chemin du servlet
        String queryString = request.getQueryString();          // paramètres GET (si présents)

        out.println("<h2>Infos sur l'URL :</h2>");
        out.println("<ul>");
        out.println("<li><b>Request URL :</b> " + requestURL + "</li>");
        // out.println("<li><b>Request URI :</b> " + requestURI + "</li>");
        // out.println("<li><b>Context Path :</b> " + contextPath + "</li>");
        // out.println("<li><b>Servlet Path :</b> " + servletPath + "</li>");
        // out.println("<li><b>Query String :</b> " + queryString + "</li>");
        out.println("</ul>");
    }
}
