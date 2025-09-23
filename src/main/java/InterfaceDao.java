package mere;

import java.sql.Connection;
import java.util.List;

public interface InterfaceDao<T> {
    void save(T entity) throws Exception;
    void update(T entity) throws Exception;
    void remove(Object id) throws Exception;
    void save(T entity, Connection con) throws Exception;
    void update(T entity, Connection con) throws Exception;
    void remove(Object id, Connection con) throws Exception;
    List<T> findAll() throws Exception;
    T findById(Object id) throws Exception;
}