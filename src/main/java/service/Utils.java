package service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import annotation.*;

public class Utils {

    public static List<UrlInfo> findMethodsAnnotatedWithGetUrl(Class<?> controllerClass) {
        List<UrlInfo> annotatedMethods = new ArrayList<>();

        try {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetURL.class)) {
                    GetURL annotation = method.getAnnotation(GetURL.class);
                    String url = annotation.url();
                    annotatedMethods.add(new UrlInfo(method, url));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return annotatedMethods;
    }

}
