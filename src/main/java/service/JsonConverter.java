package service;

import java.lang.reflect.Field;
import java.util.*;

public class JsonConverter {

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof java.sql.Date) {
            return "\"" + obj.toString() + "\"";
        }

        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }

        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }

        if (obj instanceof ModelView) {
            ModelView mv = (ModelView) obj;
            return mapToJson(mv.getData());
        }

        return objectToJson(obj);
    }

    private String objectToJson(Object obj) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = getAllFields(clazz);

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                }

                if (!first) {
                    json.append(",");
                }
                first = false;

                json.append("\"").append(field.getName()).append("\":");
                json.append(toJson(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        json.append("}");
        return json.toString();
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    private String mapToJson(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");
            json.append(toJson(entry.getValue()));
        }

        json.append("}");
        return json.toString();
    }

    private String collectionToJson(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Object item : collection) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append(toJson(item));
        }

        json.append("]");
        return json.toString();
    }

    private String arrayToJson(Object array) {
        if (array == null) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        int length = java.lang.reflect.Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                json.append(",");
            }
            Object item = java.lang.reflect.Array.get(array, i);
            json.append(toJson(item));
        }

        json.append("]");
        return json.toString();
    }

    private String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}