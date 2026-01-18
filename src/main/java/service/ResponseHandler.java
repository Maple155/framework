package service;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

import annotation.JSON;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseHandler {

    private final HttpServlet servlet;
    private final JsonConverter jsonConverter;
    private final SessionHandler sessionHandler;

    public ResponseHandler(HttpServlet servlet) {
        this.servlet = servlet;
        this.jsonConverter = new JsonConverter();
        this.sessionHandler = new SessionHandler();
    }

    public void handleResult(HttpServletRequest request, HttpServletResponse response,
            Object result, String httpMethod, Method method)
            throws ServletException, IOException {

        // Mettre à jour la session si le résultat est un ModelView
        if (result instanceof ModelView) {
            sessionHandler.updateSessionFromModelView(request, (ModelView) result);
        }

        // Vérifier si la méthode est annotée @JSON
        if (method != null && method.isAnnotationPresent(JSON.class)) {
            System.out.println("JSON annotation detected for method: " + method.getName());
            handleJsonResult(response, result);
        } else if (result instanceof ModelView) {
            handleModelView(request, response, (ModelView) result);
        } else {
            handlePlainResult(response, result, httpMethod);
        }
    }

    /**
     * Gère le résultat JSON
     */
    private void handleJsonResult(HttpServletResponse response, Object result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        String json = jsonConverter.toJson(result);
        System.out.println("Generated JSON: " + json);
        writer.print(json);
        writer.flush();
    }

    private void handleModelView(HttpServletRequest request, HttpServletResponse response, ModelView mv)
            throws ServletException, IOException {
        if (mv.getView() == null) {
            response.getWriter().println("Aucune vue spécifiée dans ModelView !");
            return;
        }

        setModelAttributes(request, mv.getData());
        forwardToView(request, response, mv.getView());
    }

    private void setModelAttributes(HttpServletRequest request, Map<String, Object> data) {
        if (data == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                request.setAttribute(key, value);
            }
        }
    }

    private void forwardToView(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = servlet.getServletContext().getRequestDispatcher("/" + view);
        dispatcher.forward(request, response);
    }

    private void handlePlainResult(HttpServletResponse response, Object result, String httpMethod)
            throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("<h3>Resultat de la methode (" + httpMethod + ") :</h3>");
        writer.println("<p>" + result.toString() + "</p>");
    }

    public void send404(HttpServletResponse response, String path, Map<String, UrlInfo> urlMap)
            throws IOException {
        PrintWriter writer = response.getWriter();

        writer.println("<!DOCTYPE html><html><body>");
        writer.println("<h1>Erreur 404 - Page non trouvée</h1>");
        writer.println("<p>Ressource introuvable : " + path + "</p>");

        if (!urlMap.isEmpty()) {
            printAvailableUrls(writer, urlMap);
        }

        writer.println("</body></html>");
    }

    public void send405(HttpServletResponse response, String httpMethod, String path, UrlInfo urlInfo)
            throws IOException {
        PrintWriter writer = response.getWriter();

        writer.println("<!DOCTYPE html><html><body>");
        writer.println("<h1>Erreur 405 - Méthode non autorisée</h1>");
        writer.println("<p>La méthode HTTP " + httpMethod + " n'est pas supportée pour l'URL : " + path + "</p>");
        writer.println("<p>Méthodes disponibles pour cette URL : " + urlInfo.getMethods().keySet() + "</p>");
        writer.println("</body></html>");
    }

    public void sendError(HttpServletResponse response, String message, Exception e)
            throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html><html><body>");
        writer.println("<h1>Erreur</h1>");
        writer.println("<p>" + message + " : " + e.getMessage() + "</p>");
        writer.println("</body></html>");
    }

    private void printAvailableUrls(PrintWriter writer, Map<String, UrlInfo> urlMap) {
        writer.println("<h2>URLs disponibles :</h2><ul>");

        for (Map.Entry<String, UrlInfo> entry : urlMap.entrySet()) {
            UrlInfo info = entry.getValue();
            writer.println("<li><strong>" + entry.getKey() + "</strong>");
            writer.println("<ul>");

            for (Map.Entry<String, Method> methodEntry : info.getMethods().entrySet()) {
                writer.println("<li>[" + methodEntry.getKey() + "] -> " +
                        info.getClassName() + "." + methodEntry.getValue().getName() + "</li>");
            }

            writer.println("</ul></li>");
        }

        writer.println("</ul>");
    }
}