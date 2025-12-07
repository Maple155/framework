package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public class ObjectBuilder {

    private final TypeConverter typeConverter;

    public ObjectBuilder(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    public Object buildFromParameters(Class<?> targetClass, String paramName,
                                     Map<String, Map<String, String>> groupedParams,
                                     HttpServletRequest request) {
        try {
            Object instance = createInstance(targetClass);
            Map<String, String> objectParams = groupedParams.get(paramName);

            if (objectParams == null || objectParams.isEmpty()) {
                return instance;
            }

            populateFields(instance, targetClass, objectParams, request, paramName);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object createInstance(Class<?> targetClass) throws Exception {
        return targetClass.getDeclaredConstructor().newInstance();
    }

    private void populateFields(Object instance, Class<?> targetClass, 
                               Map<String, String> objectParams,
                               HttpServletRequest request, String objectPrefix) {
        for (Map.Entry<String, String> entry : objectParams.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            try {
                Field field = findField(targetClass, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object convertedValue = convertFieldValue(fieldValue, field, request, objectPrefix);
                    field.set(instance, convertedValue);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du mapping du champ " + fieldName + ": " + e.getMessage());
            }
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }

    private Object convertFieldValue(String value, Field field, 
                                     HttpServletRequest request, String objectPrefix) {
        Class<?> fieldType = field.getType();

        if (fieldType.isArray()) {
            return handleArrayField(field, request, objectPrefix);
        }

        if (List.class.isAssignableFrom(fieldType)) {
            return handleListField(field, request, objectPrefix);
        }

        return typeConverter.convert(value, fieldType);
    }

    private Object handleArrayField(Field field, HttpServletRequest request, String objectPrefix) {
        String[] values = request.getParameterValues(objectPrefix + "." + field.getName());
        
        if (values == null) {
            return null;
        }

        if (field.getType().getComponentType() == String.class) {
            return values;
        }

        // Possibilité d'ajouter d'autres types de tableaux
        return values;
    }

    private List<String> handleListField(Field field, HttpServletRequest request, String objectPrefix) {
        String[] values = request.getParameterValues(objectPrefix + "." + field.getName());
        List<String> list = new ArrayList<>();

        if (values == null) {
            return list;
        }

        for (String val : values) {
            if (val != null && !val.isEmpty()) {
                list.add(val);
            }
        }

        return list;
    }
}