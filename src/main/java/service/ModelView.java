package service;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    String view;
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> session = new HashMap<>();

    public ModelView() {
    }
    
    public ModelView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addObject(String key, Object value) {
        this.data.put(key, value);
    }

    public Object getObject(String key) {
        return this.data.get(key);
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    /**
     * Ajoute ou met à jour une valeur dans la session
     */
    public void addSession(String key, Object value) {
        this.session.put(key, value);
    }

    /**
     * Récupère une valeur de la session
     */
    public Object getSessionObject(String key) {
        return this.session.get(key);
    }

    /**
     * Supprime une valeur de la session
     */
    public void removeSession(String key) {
        this.session.remove(key);
    }

    /**
     * Vérifie si une clé existe dans la session
     */
    public boolean hasSessionKey(String key) {
        return this.session.containsKey(key);
    }
}