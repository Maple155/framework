package service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import annotation.*;

public class Utils {

    public static List<UrlInfo> findMethodsAnnotatedWithGetUrl(Class<?> controllerClass) {
        List<UrlInfo> annotatedMethods = new ArrayList<>();
        
        Map<String, Map<String, Method>> urlMethodsMap = new HashMap<>();
        Map<String, Method> requestMappingMethods = new HashMap<>();

        try {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetURL.class)) {
                    GetURL annotation = method.getAnnotation(GetURL.class);
                    String url = annotation.url();
                    
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        requestMappingMethods.put(url, method);
                    } else {
                        Map<String, Method> httpMethods = urlMethodsMap.computeIfAbsent(url, k -> new HashMap<>());
                        
                        if (method.isAnnotationPresent(Get.class)) {
                            httpMethods.put("GET", method);
                        }
                        if (method.isAnnotationPresent(Post.class)) {
                            httpMethods.put("POST", method);
                        }
                        // (PUT, DELETE, etc.)
                    }
                }
            }
            
            for (Map.Entry<String, Method> entry : requestMappingMethods.entrySet()) {
                String url = entry.getKey();
                Method method = entry.getValue();
                
                Map<String, Method> methods = new HashMap<>();
                // @RequestMapping accepte toutes les méthodes HTTP
                methods.put("GET", method);
                methods.put("POST", method);
                // methods.put("PUT", method);
                // methods.put("DELETE", method);
                
                UrlInfo urlInfo = new UrlInfo(methods, url);
                urlInfo.setClassName(controllerClass.getName());
                annotatedMethods.add(urlInfo);
                
                urlMethodsMap.remove(url);
            }
            
            for (Map.Entry<String, Map<String, Method>> entry : urlMethodsMap.entrySet()) {
                String url = entry.getKey();
                Map<String, Method> methods = entry.getValue();
                
                if (!methods.isEmpty()) {
                    UrlInfo urlInfo = new UrlInfo(methods, url);
                    urlInfo.setClassName(controllerClass.getName());
                    annotatedMethods.add(urlInfo);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return annotatedMethods;
    }
}