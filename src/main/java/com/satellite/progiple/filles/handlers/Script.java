package com.satellite.progiple.filles.handlers;

import com.satellite.progiple.exceptions.FileExtensionNotCorrectException;
import com.satellite.progiple.utils.Utilities;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Script {
    final List<Line> lines = new ArrayList<>();

    @SneakyThrows
    public Script(String packageName, File file) {
        Utilities.checkFileExtension(file, "jd");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" -> ");

                Class<?> statement = Class.forName(split[0].replace("#", packageName));

                Field field;
                Class<?> methodClass;
                Object targetObject;
                if (split[1] == null || split[1].isEmpty() || split[1].equalsIgnoreCase("null")) {
                    field = null;
                    targetObject = null;
                    methodClass = statement;
                }
                else {
                    String[] fieldSplit = split[1].split(" : ");

                    field = statement.getDeclaredField(fieldSplit[0]);
                    Object value = field.get(null);
                    if (fieldSplit.length > 1) {
                        for (int i = 1; i < fieldSplit.length; i++) {
                            Object oldValue = value;
                            field = value.getClass().getDeclaredField(fieldSplit[i]);

                            field.setAccessible(true);
                            value = field.get(oldValue);
                        }
                    }

                    methodClass = value.getClass();
                    targetObject = value;
                    field.setAccessible(true);
                }

                String[] params;
                Class<?>[] classes;
                Method method;
                if (split.length <= 3 || split[3].contains("null")) {
                    params = null;
                    classes = null;
                    method = methodClass.getDeclaredMethod(split[2]);
                }
                else {
                    params = split[3].split(" : ");
                    classes = Arrays.stream(params)
                            .map(c -> {
                                String value = c.contains(".") ? c : "java.lang." + c;
                                try {
                                    return Class.forName(value.replace("#", packageName));
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toArray(Class[]::new);
                    method = methodClass.getMethod(split[2], classes);
                    params = split[4].split(" : ");
                }

                method.setAccessible(true);
                this.lines.add(new Line(
                        statement,
                        field,
                        targetObject,
                        method,
                        classes,
                        params));
            }
        }
    }

    public Object execute(int lineIndex, Object... objects) {
        return this.lines.get(lineIndex).execute(objects);
    }

    public Object[] execute(int[] lines, Object... objects) {
        Object[] result = new Object[lines.length];

        int i = 0;
        for (int line : lines) result[i++] = this.execute(line, objects);
        return result;
    }

    public record Line(Class<?> statement,
                       Field field,
                       Object object,
                       Method method,
                       Class<?>[] constructorArgs,
                       String[] args) {
        @SneakyThrows
        public Object execute(Object... objects) {
            if (args == null || constructorArgs == null) {
                return method.invoke(object, objects);
            }

            Object[] target = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                String value = args[i];
                if (value.startsWith("obj")) {
                    target[i] = objects[Integer.parseInt(value.replace("obj", ""))];
                    continue;
                }

                target[i] = switch (constructorArgs[i].getSimpleName()) {
                    case "int", "Integer" -> Integer.parseInt(value);
                    case "long", "Long" -> Long.parseLong(value);
                    case "short", "Short" -> Short.parseShort(value);
                    case "byte", "Byte" -> Byte.parseByte(value);
                    case "double", "Double" -> Double.parseDouble(value);
                    case "string", "String" -> value;
                    case "float", "Float" -> Float.parseFloat(value);
                    default -> throw new IllegalStateException("Unexpected value: " + constructorArgs[i]);
                };
            }

            return method.invoke(field == null ? null : object, target);
        }
    }
}
