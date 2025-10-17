package servlet;

import java.io.IOException;
import java.net.URL;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class FrontServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        findRessource(request, response);
    }

    private void findRessource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupère le chemin de la ressource après le contexte
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Vérifie si la ressource existe
        URL ressource = getServletContext().getResource(path);
        boolean ressourceExists = (ressource != null);

        if (ressourceExists) {
            // Forward vers le default servlet pour servir la ressource
            RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
            if (defaultDispatcher != null) {
                defaultDispatcher.forward(request, response);
            } else {
                response.getWriter().println("Dispatcher par défaut introuvable !");
            }
        } else {
            // Affiche simplement le chemin si la ressource n'existe pas
            response.getWriter().println("Ressource introuvable : " + path);
        }
    }
}
