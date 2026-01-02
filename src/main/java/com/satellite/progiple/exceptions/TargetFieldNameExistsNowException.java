package com.satellite.progiple.exceptions;

public class TargetFieldNameExistsNowException extends RuntimeException {
    public TargetFieldNameExistsNowException(String fieldName, String tableName) {
        super("Поле " + fieldName + " для таблицы " + tableName + " уже было зарегистрировано!");
    }
}
