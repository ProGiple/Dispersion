package com.satellite.progiple.databases;

import com.satellite.progiple.databases.reflection.AutoIncrement;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public interface ITableStructure {
    default void onUpdate(Table<? extends ITableStructure> table) {}
    default void onInsert(Table<? extends ITableStructure> table) {}

    @SneakyThrows
    default ITableStructure build(Object... objects) {
        Class<? extends ITableStructure> clazz = this.getClass();

        int i = 0;
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(AutoIncrement.class)) continue;

            declaredField.setAccessible(true);
            declaredField.set(this, objects[i++]);
        }

        return this;
    }
}
