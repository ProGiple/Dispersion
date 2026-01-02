package com.satellite.progiple.exceptions;

import java.io.File;

public class FileExtensionNotCorrectException extends RuntimeException {
    public FileExtensionNotCorrectException(String target, String extension) {
        super("Расширение файла " + target + " должно быть конкретно " + extension);
    }
}
