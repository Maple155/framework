package service;

import java.lang.reflect.Method;

public class UrlInfo {
    
    private String className;
    private Method method;
    private String URL;
    
    public UrlInfo() {
    }

    public UrlInfo(Method method, String uRL) {
        this.setMethod(method);
        this.setURL(uRL);
    }

    public UrlInfo(String className, Method method, String URL) {
        this.setClassName(className);
        this.setMethod(method);
        this.setURL(URL);
    }
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

}
