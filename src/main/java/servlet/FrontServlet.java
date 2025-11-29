package servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
            // ScanController scanController = new ScanController();
            List<Class<?>> classAnnotated = ScanController.findAllClassesWithAnnotation(getServletContext(),
                    Controller.class);

            for (Class<?> clazz : classAnnotated) {
                List<UrlInfo> methods = Utils.findMethodsAnnotatedWithGetUrl(clazz);

                for (UrlInfo urlInfo : methods) {
                    urlMap.put(urlInfo.getURL(), urlInfo);
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
            findRessource(request, response, "GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            findRessource(request, response, "POST");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findRessource(HttpServletRequest request, HttpServletResponse response, String httpMethod)
            throws ServletException, IOException {

        String path = request.getRequestURI().substring(request.getContextPath().length());
        response.setContentType("text/html;charset=UTF-8");

        UrlMatcher.MatchResult matchResult = UrlMatcher.findMatch(path, urlMap);
        
        if (matchResult != null) {
            UrlInfo urlInfo = matchResult.getUrlInfo();
            Map<String, String> urlParameters = matchResult.getParameters();

            // Récupérer la méthode correspondant au type HTTP
            Method urlMethod = urlInfo.getMethod(httpMethod);
            
            if (urlMethod == null) {
                response.getWriter().println("<!DOCTYPE html><html><body>");
                response.getWriter().println("<h1>Erreur 405 - Méthode non autorisée</h1>");
                response.getWriter().println("<p>La méthode HTTP " + httpMethod + " n'est pas supportée pour l'URL : " + path + "</p>");
                response.getWriter().println("<p>Méthodes disponibles pour cette URL : " + urlInfo.getMethods().keySet() + "</p>");
                response.getWriter().println("</body></html>");
                return;
            }

            try {
                Class<?> controllerClass = Class.forName(urlInfo.getClassName());
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                Object[] methodArgs = prepareMethodArguments(urlMethod, request, urlParameters);

                Object result = urlMethod.invoke(controllerInstance, methodArgs);

                if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;

                    if (mv.getView() != null) {
                        Map<String, Object> data = mv.getData();
                        
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                if (key != null && value != null) {
                                    request.setAttribute(key, value);
                                }
                            }
                        }
                        
                        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/" + mv.getView());
                        dispatcher.forward(request, response);
                        return;
                    } else {
                        response.getWriter().println("Aucune vue spécifiée dans ModelView !");
                    }
                } else {
                    response.getWriter().println("<h3>Résultat de la méthode (" + httpMethod + ") :</h3>");
                    response.getWriter().println("<p>" + result.toString() + "</p>");
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
                response.getWriter().println("<li><strong>" + entry.getKey() + "</strong>");
                response.getWriter().println("<ul>");
                for (Map.Entry<String, Method> methodEntry : info.getMethods().entrySet()) {
                    response.getWriter().println("<li>[" + methodEntry.getKey() + "] -> " + 
                        info.getClassName() + "." + methodEntry.getValue().getName() + "</li>");
                }
                response.getWriter().println("</ul></li>");
            }
            response.getWriter().println("</ul>");
        }

        response.getWriter().println("</body></html>");
    }

    private Object[] prepareMethodArguments(Method method, HttpServletRequest request, Map<String, String> urlParameters) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = getParameterName(param);
            String paramValue = getParameterValue(paramName, request, urlParameters);
            
            if (param.getType() == Object.class) {
                args[i] = paramValue;
            } else {
                args[i] = convertValue(paramValue, param.getType());
            }
        }
        
        return args;
    }

    private String getParameterName(Parameter parameter) {
        if (parameter.isAnnotationPresent(Param.class)) {
            Param paramAnnotation = parameter.getAnnotation(Param.class);
            String annotationValue = paramAnnotation.value();
            if (!annotationValue.isEmpty()) {
                return annotationValue;
            }
        }
        
        String name = parameter.getName();
                
        return name;
    }

    private String getParameterValue(String paramName, HttpServletRequest request, Map<String, String> urlParameters) {
        String value = urlParameters.get(paramName);
        
        if (value == null) {
            value = request.getParameter(paramName);
        }
        
        return value;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
            return getDefaultValue(targetType);
        }
        
        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(value);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == float.class || targetType == Float.class) {
                return Float.parseFloat(value);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (targetType == Object.class) {
                return value;
            }
        } catch (NumberFormatException e) {
            return getDefaultValue(targetType);
        }
        
        return getDefaultValue(targetType);
    }

    private Object getDefaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        return null;
    }
}