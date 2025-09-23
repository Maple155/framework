package dao;

import java.sql.*;
import java.util.*;
import java.lang.reflect.Field;
import mere.*;
import models.*;
import utils.*;

public class GenericDao<T>  implements InterfaceDao<T>{
    private final Class<T> entityClass;

    // Correction du constructeur avec le paramètre de type approprié
    public GenericDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) throws Exception {
        Connection con = null;
        Statement stmt = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            String query = QueryBuilder.buildInsertQuery(entity);
            stmt.executeUpdate(query);
        } finally {
            closeResources(con, stmt, null);
        }
    }

    @Override
    public void save(T entity, Connection con) throws Exception {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            String query = QueryBuilder.buildInsertQuery(entity);
            stmt.executeUpdate(query);
        } finally {
            closeResources(con, stmt, null);
        }
    }

    @Override
    public List<T> findAll() throws Exception, ReflectiveOperationException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(QueryBuilder.buildSelectAllQuery(entityClass));
            return mapResultSetToEntities(rs);
        } finally {
            closeResources(con, stmt, rs);
        }
    }

    @Override
    public T findById(Object id) throws Exception, ReflectiveOperationException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(QueryBuilder.buildSelectByIdQuery(entityClass, id));
            return rs.next() ? mapRowToEntity(rs) : null;
        } finally {
            closeResources(con, stmt, rs);
        }
    }

    @Override
    public void update(T entity) throws Exception, ReflectiveOperationException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            String query = QueryBuilder.buildUpdateQuery(entity);
            stmt.executeUpdate(query);
        } finally {
            closeResources(con, stmt, null);
        }
    }

    @Override
    public void update(T entity, Connection con) throws Exception, ReflectiveOperationException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            String query = QueryBuilder.buildUpdateQuery(entity);
            stmt.executeUpdate(query);
        } finally {
            closeResources(con, stmt, null);
        }
    }

    @Override
    public void remove(Object id) throws Exception {
        Connection con = null;
        Statement stmt = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(QueryBuilder.buildDeleteQuery(entityClass, id));
        } finally {
            closeResources(con, stmt, null);
        }
    }

    @Override
    public void remove(Object id, Connection con) throws Exception {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(QueryBuilder.buildDeleteQuery(entityClass, id));
        } finally {
            closeResources(con, stmt, null);
        }
    }

    public List<T> findPaginated(int page, int size) throws Exception, ReflectiveOperationException {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = UtilsDao.createConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(QueryBuilder.buildPaginatedQuery(entityClass, page, size));
            return mapResultSetToEntities(rs);
        } finally {
            closeResources(con, stmt, rs);
        }
    }

    private List<T> mapResultSetToEntities(ResultSet rs) throws Exception, ReflectiveOperationException {
        List<T> entities = new ArrayList<>();
        while (rs.next()) {
            entities.add(mapRowToEntity(rs));
        }
        return entities;
    }

    private T mapRowToEntity(ResultSet rs) throws Exception, ReflectiveOperationException {
        T entity = entityClass.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object columnValue = rs.getObject(i);
            setFieldValue(entity, columnName, columnValue);
        }
        return entity;
    }

    private void setFieldValue(T entity, String fieldName, Object value) throws ReflectiveOperationException {
        Class<?> currentClass = entity.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, value);
                return;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
    }

    private void closeResources(Connection con, Statement stmt, ResultSet rs) throws Exception {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (con != null) UtilsDao.closeConnection(con);
    }
}