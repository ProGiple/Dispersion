package com.satellite.progiple.filles;

import com.satellite.progiple.exceptions.FileExtensionNotCorrectException;
import com.satellite.progiple.filles.handlers.EnvStorage;
import com.satellite.progiple.filles.handlers.Script;
import com.satellite.progiple.filles.reflection.ScriptData;
import com.satellite.progiple.utils.Utilities;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@UtilityClass
public class DiFiles {
    @SneakyThrows
    public EnvStorage loadEnv(String path) {
        Utilities.checkFileExtension(path, "env");

        EnvStorage storage = new EnvStorage();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    storage.add(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return storage;
    }

    public Script loadScript(String packageName, String path) {
        Utilities.checkFileExtension(path, "jd");
        return new Script(packageName, new File(path));
    }
}
