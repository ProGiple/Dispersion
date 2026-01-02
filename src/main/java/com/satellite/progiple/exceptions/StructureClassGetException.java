package com.satellite.progiple.exceptions;

public class StructureClassGetException extends NullPointerException {
    public StructureClassGetException(String tableName) {
        super("Не удалось получить класс структуры для таблицы " + tableName);
    }
}
