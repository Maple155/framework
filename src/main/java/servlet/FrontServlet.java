package servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.*;
import annotation.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

@MultipartConfig
public class FrontServlet extends HttpServlet {

    private Map<String, UrlInfo> urlMap;
    private ParameterMapper parameterMapper;
    private ResponseHandler responseHandler;
    private FileHandler fileHandler;

    @Override
    public void init() throws ServletException {
        urlMap = new HashMap<>();
        parameterMapper = new ParameterMapper();
        responseHandler = new ResponseHandler(this);
        fileHandler = new FileHandler();

        initializeControllers();
    }

    private void initializeControllers() {
        try {
            List<Class<?>> classAnnotated = ScanController.findAllClassesWithAnnotation(
                    getServletContext(),
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
        processRequest(request, response, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "POST");
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String httpMethod)
            throws ServletException, IOException {
        try {
            String path = extractPath(request);
            response.setContentType("text/html;charset=UTF-8");

            UrlMatcher.MatchResult matchResult = UrlMatcher.findMatch(path, urlMap);

            if (matchResult != null) {
                handleMappedUrl(request, response, httpMethod, path, matchResult);
            } else if (isStaticResource(path)) {
                forwardToDefaultServlet(request, response);
            } else {
                responseHandler.send404(response, path, urlMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseHandler.sendError(response, "Erreur lors du traitement de la requête", e);
        }
    }

    private String extractPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    private void handleMappedUrl(HttpServletRequest request, HttpServletResponse response,
            String httpMethod, String path, UrlMatcher.MatchResult matchResult)
            throws ServletException, IOException {

        UrlInfo urlInfo = matchResult.getUrlInfo();
        Map<String, String> urlParameters = matchResult.getParameters();
        Method urlMethod = urlInfo.getMethod(httpMethod);

        if (urlMethod == null) {
            responseHandler.send405(response, httpMethod, path, urlInfo);
            return;
        }

        invokeControllerMethod(request, response, httpMethod, urlInfo, urlMethod, urlParameters);
    }

    private void invokeControllerMethod(HttpServletRequest request, HttpServletResponse response,
            String httpMethod, UrlInfo urlInfo, Method urlMethod,
            Map<String, String> urlParameters)
            throws ServletException, IOException {
        try {
            System.out.println("=== Invoking Controller Method ===");
            System.out.println("URL: " + urlInfo.getURL());
            System.out.println("HTTP Method: " + httpMethod);
            System.out.println("Controller Method: " + urlMethod);
            System.out.println(
                    "Has @JSON annotation: " + (urlMethod != null && urlMethod.isAnnotationPresent(JSON.class)));

            // Vérifier et extraire les fichiers uploadés
            Map<String, byte[]> uploadedFiles = new HashMap<>();
            try {
                uploadedFiles = fileHandler.extractUploadedFiles(request);
                
                // Sauvegarder les fichiers si présents
                if (!uploadedFiles.isEmpty()) {
                    String uploadPath = getServletContext().getRealPath("/WEB-INF/uploads");
                    fileHandler.saveAllFiles(uploadPath, uploadedFiles);
                    System.out.println("Nombre de fichiers uploadés: " + uploadedFiles.size());
                }
            } catch (Exception e) {
                System.out.println("Aucun fichier uploadé ou erreur: " + e.getMessage());
            }

            Object controllerInstance = createControllerInstance(urlInfo);
            Object[] methodArgs = parameterMapper.prepareMethodArguments(urlMethod, request, 
                                                                        urlParameters, uploadedFiles);
            Object result = urlMethod.invoke(controllerInstance, methodArgs);

            System.out.println("Result type: " + (result != null ? result.getClass().getName() : "null"));

            responseHandler.handleResult(request, response, result, httpMethod, urlMethod);
        } catch (Exception e) {
            e.printStackTrace();
            responseHandler.sendError(response, "Erreur lors de l'exécution de la méthode", e);
        }
    }

    private Object createControllerInstance(UrlInfo urlInfo) throws Exception {
        Class<?> controllerClass = Class.forName(urlInfo.getClassName());
        return controllerClass.getDeclaredConstructor().newInstance();
    }

    private boolean isStaticResource(String path) {
        try {
            URL resource = getServletContext().getResource(path);
            return resource != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void forwardToDefaultServlet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher defaultDispatcher = getServletContext().getNamedDispatcher("default");
        if (defaultDispatcher != null) {
            defaultDispatcher.forward(request, response);
        }
    }
}