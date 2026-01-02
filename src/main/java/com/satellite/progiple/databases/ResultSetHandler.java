package com.satellite.progiple.databases;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetHandler<R> {
    R handle(ResultSet resultSet) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException;
}