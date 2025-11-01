package servlet;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import service.*;
import annotation.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class FrontServlet extends HttpServlet {

    private Map<String, UrlInfo> urlMap;

    @Override
    public void init() throws ServletException {
        urlMap = new HashMap<>();
        ScanController scanController = new ScanController();
        // List<String> packagesPath =
        // scanController.readPackagesFromXML(getServletContext());

        // for (String packagePath : packagesPath) {
        List<Class<?>> classAnnotated = ScanController.findAllClassesWithAnnotation(getServletContext(),
                Controller.class);

        for (Class<?> clazz : classAnnotated) {
            List<UrlInfo> methods = Utils.findMethodsAnnotatedWithGetUrl(clazz);

            for (UrlInfo method : methods) {
                method.setClassName(clazz.getSimpleName());
                urlMap.put(method.getURL(), method);
            }
        }
        // }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        findRessource(request, response);
    }

    private void findRessource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getRequestURI().substring(request.getContextPath().length());
        URL ressource = getServletContext().getResource(path);
        boolean ressourceExists = (ressource != null);

        if (urlMap.containsKey(path)) {
            if (ressourceExists) {
                RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");

                if (defaultDispatcher != null) {
                    defaultDispatcher.forward(request, response);
                }
            }

            response.setContentType("text/html;charset=UTF-8");
            UrlInfo urlInfo = urlMap.get(path);

            response.getWriter().println("URL trouvée !");
            response.getWriter().println("Classe : " + urlInfo.getClassName());
            response.getWriter().println("Méthode : " + urlInfo.getMethod().getName());
            response.getWriter().println("URL : " + urlInfo.getURL());

        } else {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<!DOCTYPE html><html><body>");
            response.getWriter().println("<h1>Erreur 404 - Page non trouvée</h1>");
            response.getWriter().println("<p>Ressource introuvable : " + path + "</p>");

            if (!urlMap.isEmpty()) {
                response.getWriter().println("<h2>URLs disponibles :</h2><ul>");
                for (Map.Entry<String, UrlInfo> entry : urlMap.entrySet()) {
                    UrlInfo info = entry.getValue();
                    response.getWriter().println("<li><a href='" + entry.getKey() + "'>" + entry.getKey() + "</a>");
                    response.getWriter()
                            .println(" -> " + info.getClassName() + "." + info.getMethod().getName() + "</li>");
                }
                response.getWriter().println("</ul>");
            }

            response.getWriter().println("</body></html>");
        }
    }
}