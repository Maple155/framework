package service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Gestionnaire de session pour convertir entre HttpSession et Map
 */
public class SessionHandler {

    /**
     * Extrait toutes les données de la session HTTP dans une Map
     */
    public Map<String, Object> extractSessionData(HttpServletRequest request) {
        Map<String, Object> sessionData = new HashMap<>();
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = session.getAttribute(key);
                sessionData.put(key, value);
            }
        }
        
        return sessionData;
    }

    /**
     * Met à jour la session HTTP avec les données de la Map
     */
    public void updateSessionData(HttpServletRequest request, Map<String, Object> sessionData) {
        if (sessionData == null || sessionData.isEmpty()) {
            return;
        }
        
        HttpSession session = request.getSession(true);
        
        for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value == null) {
                // Si la valeur est null, supprimer l'attribut de session
                session.removeAttribute(key);
            } else {
                session.setAttribute(key, value);
            }
        }
    }

    /**
     * Injecte les données de session dans un ModelView
     */
    public void injectSessionIntoModelView(HttpServletRequest request, ModelView modelView) {
        if (modelView != null) {
            Map<String, Object> sessionData = extractSessionData(request);
            modelView.setSession(sessionData);
        }
    }

    /**
     * Met à jour la session HTTP depuis un ModelView
     */
    public void updateSessionFromModelView(HttpServletRequest request, ModelView modelView) {
        if (modelView != null && modelView.getSession() != null) {
            updateSessionData(request, modelView.getSession());
        }
    }
}