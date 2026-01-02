package com.satellite.progiple.utils;

import com.satellite.progiple.exceptions.FileExtensionNotCorrectException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.lang.reflect.Field;

@UtilityClass
public class Utilities {
    @SneakyThrows
    public String toString(Object o) {
        StringBuilder sb = new StringBuilder();

        for (Field field : o.getClass().getDeclaredFields()) {
            if (!sb.isEmpty()) sb.append(", ");

            field.setAccessible(true);
            sb.append(field.getName()).append("=").append(field.get(o));
        }
        sb.insert(0, o.getClass().getSimpleName() + "{").append("}");

        return sb.toString();
    }

    public void checkFileExtension(String path, String extension) {
        extension = "." + extension;
        if (!path.endsWith(extension))
            throw new FileExtensionNotCorrectException(path, extension);
    }

    public void checkFileExtension(File file, String extension) {
        checkFileExtension(file.getAbsolutePath(), extension);
    }
}
