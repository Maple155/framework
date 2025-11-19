package service;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    String view;
    Map<String, Object> data = new HashMap<>();

    String six = "6";

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

    public Object getObject (String key) {
        return this.data.get(key);
    }; 

}
