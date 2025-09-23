package utils;

import java.lang.reflect.Field;

public class QueryBuilder {
    private QueryBuilder() {}

    public static <T> String buildSelectAllQuery(Class<T> entityClass) {
        return "SELECT * FROM " + getTableName(entityClass) + " ORDER BY id ASC";
    }

    public static <T> String buildSelectByIdQuery(Class<T> entityClass, Object id) {
        return "SELECT * FROM " + getTableName(entityClass) + " WHERE id = " + formatValue(id);
    }

    public static <T> String buildPaginatedQuery(Class<T> entityClass, int page, int size) {
        int offset = (page - 1) * size;
        return "SELECT * FROM " + getTableName(entityClass) + 
               " LIMIT " + size + " OFFSET " + offset;
    }

    public static <T> String buildInsertQuery(T entity) throws ReflectiveOperationException {
        Class<?> entityClass = entity.getClass();
        Field[] fields = entityClass.getDeclaredFields();
        String tableName = getTableName(entityClass);
        
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isIdField(field, tableName)) {
                columns.append(field.getName()).append(", ");
                values.append(formatValue(field.get(entity))).append(", ");
            }
        }
        
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                removeLastComma(columns),
                removeLastComma(values));
    }

    public static <T> String buildUpdateQuery(T entity) throws ReflectiveOperationException {
        Class<?> entityClass = entity.getClass();
        Field[] fields = entityClass.getDeclaredFields();
        String tableName = getTableName(entityClass);
        Object id = getIdValue(entity, fields);
        
        StringBuilder setClause = new StringBuilder();
        
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isIdField(field, tableName)) {
                setClause.append(field.getName())
                         .append(" = ")
                         .append(formatValue(field.get(entity)))
                         .append(", ");
            }
        }
        
        return String.format("UPDATE %s SET %s WHERE id = %s",
                tableName,
                removeLastComma(setClause),
                formatValue(id));
    }

    public static <T> String buildDeleteQuery(Class<T> entityClass, Object id) {
        return "DELETE FROM " + getTableName(entityClass) + " WHERE id = " + formatValue(id);
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName().toLowerCase();
    }

    private static boolean isIdField(Field field, String tableName) {
        return field.getName().equals("id") || field.getName().equals("id_" + tableName);
    }

    private static Object getIdValue(Object entity, Field[] fields) throws ReflectiveOperationException {
        for (Field field : fields) {
            if (isIdField(field, getTableName(entity.getClass()))) {
                field.setAccessible(true);
                return field.get(entity);
            }
        }
        throw new RuntimeException("No ID field found");
    }

    private static String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof String) return "'" + value + "'";
        return value.toString();
    }

    private static String removeLastComma(StringBuilder sb) {
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }
}