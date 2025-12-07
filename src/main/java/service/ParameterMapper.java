package service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.MapParam;
import annotation.Param;
import jakarta.servlet.http.HttpServletRequest;

public class ParameterMapper {

    
    private final TypeConverter typeConverter;
    private final ObjectBuilder objectBuilder;

    public ParameterMapper() {
        this.typeConverter = new TypeConverter();
        this.objectBuilder = new ObjectBuilder(typeConverter);
    }

    public Object[] prepareMethodArguments(Method method, HttpServletRequest request,
                                          Map<String, String> urlParameters) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, Map<String, String>> groupedParams = groupParametersByPrefix(request);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = getParameterName(param);

            args[i] = resolveParameterValue(param, paramName, request, urlParameters, groupedParams);
        }
        
        return args;
    }

    private Object resolveParameterValue(Parameter param, String paramName, 
                                        HttpServletRequest request,
                                        Map<String, String> urlParameters,
                                        Map<String, Map<String, String>> groupedParams) {
        if (param.isAnnotationPresent(MapParam.class)) {
            return buildMapParam(request);
        } else if (isComplexType(param.getType())) {
            return objectBuilder.buildFromParameters(param.getType(), paramName, groupedParams, request);
        } else {
            String paramValue = getParameterValue(paramName, request, urlParameters);
            return typeConverter.convert(paramValue, param.getType());
        }
    }

    private Map<String, Map<String, String>> groupParametersByPrefix(HttpServletRequest request) {
        Map<String, Map<String, String>> grouped = new HashMap<>();
        Map<String, String[]> allParams = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : allParams.entrySet()) {
            String fullName = entry.getKey();
            String[] values = entry.getValue();

            if (fullName.contains(".")) {
                String[] parts = fullName.split("\\.", 2);
                String prefix = parts[0];
                String fieldName = parts[1];

                grouped.putIfAbsent(prefix, new HashMap<>());
                grouped.get(prefix).put(fieldName, values[0]);
            }
        }

        return grouped;
    }

    private Map<String, Object> buildMapParam(HttpServletRequest request) {
        Map<String, Object> mapParam = new HashMap<>();
        Map<String, String[]> allParams = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : allParams.entrySet()) {
            mapParam.put(entry.getKey(), entry.getValue());
        }

        return mapParam;
    }

    private String getParameterName(Parameter parameter) {
        if (parameter.isAnnotationPresent(Param.class)) {
            Param paramAnnotation = parameter.getAnnotation(Param.class);
            String annotationValue = paramAnnotation.value();
            if (!annotationValue.isEmpty()) {
                return annotationValue;
            }
        }
        return parameter.getName();
    }

    private String getParameterValue(String paramName, HttpServletRequest request, 
                                     Map<String, String> urlParameters) {
        String value = urlParameters.get(paramName);
        if (value == null) {
            value = request.getParameter(paramName);
        }
        return value;
    }

    private boolean isComplexType(Class<?> type) {
        return !type.isPrimitive() 
            && type != String.class 
            && type != Integer.class 
            && type != Long.class 
            && type != Double.class 
            && type != Float.class 
            && type != Boolean.class 
            && type != Object.class
            && !type.isArray()
            && !List.class.isAssignableFrom(type)
            && !Map.class.isAssignableFrom(type);
    }
}