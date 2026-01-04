package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class FileHandler {
    
    private static final int BUFFER_SIZE = 8192;
    
    /**
     * Extrait tous les fichiers uploadés depuis la requête
     * @return Map avec nom du fichier comme clé et contenu en byte[] comme valeur
     */
    public Map<String, byte[]> extractUploadedFiles(HttpServletRequest request) 
            throws IOException, ServletException {
        Map<String, byte[]> filesMap = new HashMap<>();
        
        Collection<Part> parts = request.getParts();
        
        for (Part part : parts) {
            String fileName = getFileName(part);
            
            if (fileName != null && !fileName.isEmpty()) {
                byte[] fileData = readPartContent(part);
                filesMap.put(fileName, fileData);
                
                System.out.println("Fichier uploade: " + fileName + " (" + fileData.length + " bytes)");
            }
        }
        
        return filesMap;
    }
    
    /**
     * Lit le contenu d'une Part en byte[]
     */
    private byte[] readPartContent(Part part) throws IOException {
        try (InputStream inputStream = part.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }
    
    /**
     * Extrait le nom du fichier depuis une Part
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        
        if (contentDisposition == null) {
            return null;
        }
        
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                String fileName = content.substring(content.indexOf('=') + 1).trim();
                return fileName.replace("\"", "");
            }
        }
        
        return null;
    }
    
    /**
     * Sauvegarde un fichier dans WEB-INF/uploads
     */
    public void saveFile(String uploadPath, String fileName, byte[] fileData) throws IOException {
        File uploadDir = new File(uploadPath);
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        File file = new File(uploadDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileData);
        }
        
        System.out.println("Fichier sauvegarde: " + file.getAbsolutePath());
    }
    
    /**
     * Sauvegarde tous les fichiers depuis la map
     */
    public void saveAllFiles(String uploadPath, Map<String, byte[]> filesMap) throws IOException {
        for (Map.Entry<String, byte[]> entry : filesMap.entrySet()) {
            saveFile(uploadPath, entry.getKey(), entry.getValue());
        }
    }
}