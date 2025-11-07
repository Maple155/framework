package service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletContext;

public class ScanController {
    
    public ScanController() {}

    public static <T extends Annotation> List<Class<?>> findAllClassesWithAnnotation(
            ServletContext servletContext, Class<T> annotationClass) {
        List<Class<?>> classes = new ArrayList<>();
        
        try {
            String classesPath = servletContext.getRealPath("/WEB-INF/classes");
            
            if (classesPath == null) {
                System.err.println("WEB-INF/classes introuvable");
                return classes;
            }
            
            File classesDir = new File(classesPath);
            
            if (!classesDir.exists() || !classesDir.isDirectory()) {
                System.err.println("Le répertoire WEB-INF/classes n'existe pas");
                return classes;
            }
            
            scanDirectoryForAnnotation(classesDir, "", classes, annotationClass);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du scan automatique :");
            e.printStackTrace();
        }
        
        return classes;
    }

    private static <T extends Annotation> void scanDirectoryForAnnotation(
            File directory, String packageName, List<Class<?>> classes, Class<T> annotationClass) {
        
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() 
                    ? file.getName() 
                    : packageName + "." + file.getName();
                    
                scanDirectoryForAnnotation(file, newPackage, classes, annotationClass);
                
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                
                try {
                    Class<?> clazz = Class.forName(className);
                    
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                } catch (Exception e) {
                }
            }
        }
    }
}