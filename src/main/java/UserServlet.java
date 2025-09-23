package servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import models.*;
import utils.*;
import dao.*;
import java.util.*;

public class UserServlet extends HttpServlet{
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // res.setContentType("text/html");     
        try {            
            GenericDao genericDao = new GenericDao(Personne.class);
            String nom = req.getParameter("nom");
            Personne p = new Personne();
            List<Personne> user_list = genericDao.findAll();
            HttpSession session = req.getSession();
            boolean isAuthenticate = false;
            for (int i = 0; i < user_list.size(); i++) {
                if (user_list.get(i).getNom().equalsIgnoreCase(nom)) {
                    isAuthenticate = true;
                    session.setAttribute("nom", nom);
                    RequestDispatcher dispat = req.getRequestDispatcher("/receiver");
                    dispat.forward(req, res);
                    break;
                }
            }

            if (!isAuthenticate) {                
                RequestDispatcher dispat = req.getRequestDispatcher("/index.jsp?error=1");
                dispat.forward(req, res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Override
    // protected void doPost(HttpServletRequest req, HttpServletResponse res)
    //         throws ServletException, IOException {
    // }
}
