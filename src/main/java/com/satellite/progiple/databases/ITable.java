package com.satellite.progiple.databases;

import java.util.Collection;
import java.util.List;

public interface ITable<S extends ITableStructure> {
    boolean isAsync();
    void loadTable();
    void insert(S str, boolean async);
    default void insert(S str) {
        this.insert(str, isAsync());
    }
    void update(S str, String whereCondition, boolean async);
    default void update(S str, String whereCondition) {
        this.update(str, whereCondition, isAsync());
    }
    void delete(String whereCondition, boolean async);
    default void delete(String whereCondition) {
        this.delete(whereCondition, isAsync());
    }
    void request(String query, boolean async, Object... objects);
    <E> List<E> get(String whereCondition, ResultSetHandler<E> handler);
    Collection<S> get(String whereCondition);
    void clear();
    void drop();
}
