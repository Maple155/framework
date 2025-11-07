package servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        try {

            ScanController scanController = new ScanController();
            List<Class<?>> classAnnotated = ScanController.findAllClassesWithAnnotation(getServletContext(),
                    Controller.class);

            for (Class<?> clazz : classAnnotated) {
                List<UrlInfo> methods = Utils.findMethodsAnnotatedWithGetUrl(clazz);

                for (UrlInfo method : methods) {
                    method.setClassName(clazz.getName());
                    urlMap.put(method.getURL(), method);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            findRessource(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findRessource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getRequestURI().substring(request.getContextPath().length());
        response.setContentType("text/html;charset=UTF-8");

        if (urlMap.containsKey(path)) {
            UrlInfo urlInfo = urlMap.get(path);
            Method urlMethod = urlInfo.getMethod();

            try {
                Class<?> controllerClass = Class.forName(urlInfo.getClassName());
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                Object result = urlMethod.invoke(controllerInstance);

                if (result instanceof String) {
                    response.getWriter().println(result);
                } else if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;

                    if (mv.getView() != null) {
                        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/" + mv.getView());
                        dispatcher.forward(request, response);
                        return;
                    } else {
                        response.getWriter().println("Aucune vue spécifiée dans ModelView !");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().println("Erreur lors du traitement de la requête : " + e.getMessage());
            }
            return;
        }

        URL ressource = getServletContext().getResource(path);
        if (ressource != null) {
            RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
            if (defaultDispatcher != null) {
                defaultDispatcher.forward(request, response);
                return;
            }
        }

        response.getWriter().println("<!DOCTYPE html><html><body>");
        response.getWriter().println("<h1>Erreur 404 - Page non trouvée</h1>");
        response.getWriter().println("<p>Ressource introuvable : " + path + "</p>");

        if (!urlMap.isEmpty()) {
            response.getWriter().println("<h2>URLs disponibles :</h2><ul>");
            for (Map.Entry<String, UrlInfo> entry : urlMap.entrySet()) {
                UrlInfo info = entry.getValue();
                response.getWriter().println("<li><a href='" + entry.getKey() + "'>" + entry.getKey() + "</a>");
                response.getWriter().println(" -> " + info.getClassName() + "." + info.getMethod().getName() + "</li>");
            }
            response.getWriter().println("</ul>");
        }

        response.getWriter().println("</body></html>");
    }

}