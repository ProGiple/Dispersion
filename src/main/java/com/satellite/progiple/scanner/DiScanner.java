package com.satellite.progiple.scanner;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class DiScanner {

    @SneakyThrows
    public static Set<Class<?>> scanPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File dir = new File(resource.toURI());
            if (dir.exists()) {
                scanDirectory(packageName, dir, classes);
            }
        }

        return classes;
    }


    private void scanDirectory(String packageName, File dir, Set<Class<?>> classes) throws ClassNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(packageName + "." + file.getName(), file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
    }
}
