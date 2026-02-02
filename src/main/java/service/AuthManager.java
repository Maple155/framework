package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Gestionnaire d'authentification et d'autorisation
 */
public class AuthManager {
    
    private final String userSessionKey;
    private final String roleSessionKey;
    private final String loginPageUrl;
    
    public AuthManager(String userSessionKey, String roleSessionKey, String loginPageUrl) {
        this.userSessionKey = userSessionKey;
        this.roleSessionKey = roleSessionKey;
        this.loginPageUrl = loginPageUrl;
    }
    
    /**
     * Vérifie si l'utilisateur est authentifié
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(userSessionKey) != null;
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle requis
     */
    public boolean hasRequiredRole(HttpServletRequest request, String[] requiredRoles) {
        if (requiredRoles == null || requiredRoles.length == 0) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        List<String> userRoles = (List<String>) session.getAttribute(roleSessionKey);
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        
        // Vérifier si l'utilisateur a au moins un des rôles requis
        return userRoles.stream()
                .anyMatch(userRole -> Arrays.asList(requiredRoles).contains(userRole));
    }
    
    /**
     * Redirige vers la page de login
     */
    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + loginPageUrl);
    }
    
    /**
     * Connecte un utilisateur
     */
    public void loginUser(HttpServletRequest request, String username, List<String> roles) {
        HttpSession session = request.getSession(true);
        session.setAttribute(userSessionKey, username);
        session.setAttribute(roleSessionKey, roles);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
    }
    
    /**
     * Déconnecte un utilisateur
     */
    public void logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Récupère l'utilisateur connecté
     */
    public String getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(userSessionKey);
        }
        return null;
    }
    
    /**
     * Récupère les rôles de l'utilisateur connecté
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (List<String>) session.getAttribute(roleSessionKey);
        }
        return null;
    }
}