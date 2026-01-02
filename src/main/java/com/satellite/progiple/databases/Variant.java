package com.satellite.progiple.databases;

import lombok.AllArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

@AllArgsConstructor
public enum Variant {
    INT(int.class, Integer.class),
    DOUBLE(double.class, Double.class),
    FLOAT(float.class, Float.class),
    LONG(long.class, Long.class),
    SHORT(short.class, Short.class),
    BYTE(byte.class, Byte.class),
    VARCHAR(String.class),
    TEXT(String.class),
    MEDIUMTEXT(String.class),
    LONGTEXT(String.class),
    TIME(Time.class),
    DATE(Date.class),
    TIMESTAMP(Timestamp.class),
    ENUM(Enum.class);

    private final Class<?> main;
    private final Class<?> secondary;

    Variant(Class<?> main) {
        this(main, null);
    }

    public static Variant get(Field field) {
        Class<?> type = field.getType();
        if (type == String.class) {
            if (field.isAnnotationPresent(Varchar.class)) return VARCHAR;

            Text text = field.getAnnotation(Text.class);
            if (text == null) return TEXT;

            return switch (text.value()) {
                case Text.Type.MEDIUM -> MEDIUMTEXT;
                case Text.Type.LONG -> LONGTEXT;
                default -> TEXT;
            };
        }
        else {
            return Arrays.stream(Variant.values())
                    .filter(v -> v.main.isAssignableFrom(type) || type.equals(v.secondary))
                    .findFirst()
                    .orElse(null);
        }
    }

    public static int getVarchar(Field field) {
        return field.getAnnotation(Varchar.class).value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Text {
        Type value();
        public enum Type {
            TEXT,
            MEDIUM,
            LONG;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Varchar {
        int value();
    }
}
