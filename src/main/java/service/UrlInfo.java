package service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UrlInfo {
    
    private String className;
    // private Method method;
    private String URL;
    private Map<String, Method> methods = new HashMap<>();
    
    
    public UrlInfo() {
    }

    public UrlInfo(Map<String, Method> methods, String url) {
        // this.setMethod(method);
        this.setMethods(methods);
        this.setURL(url);
    }

    public UrlInfo(String className, Map<String, Method> methods, String url) {
        this.setClassName(className);
        // this.setMethod(method);
        this.setMethods(methods);
        this.setURL(url);
    }
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    // public Method getMethod() {
    //     return method;
    // }
    // public void setMethod(Method method) {
    //     this.method = method;
    // }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        URL = url;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, Method> methods) {
        this.methods = methods;
    }

    public void putMethod (String httpMethod, Method method) {
        this.methods.put(httpMethod, method);
    }

    public boolean containsKey (String key) {
        if (this.methods.containsKey(key)) {
            return true;
        }

        return false;
    }

    public Method getMethod (String key) {

        if (this.containsKey(key)) {
            return this.methods.get(key);
        }

        return null;

    }
}
