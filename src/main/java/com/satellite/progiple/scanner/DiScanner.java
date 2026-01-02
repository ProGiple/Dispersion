package com.satellite.progiple.scanner;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class DiScanner {

    public static Set<Class<?>> scanPackage(String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            if ("file".equals(protocol)) {
                File dir = new File(resource.getFile());
                scanDirectory(packageName, dir, classes);
            } else if ("jar".equals(protocol)) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (JarFile jar = new JarFile(jarPath)) {
                    scanJar(packageName, jar, classes);
                }
            }
        }
        return classes;
    }

    private static void scanDirectory(String packageName, File dir, Set<Class<?>> classes) throws ClassNotFoundException {
        if (!dir.exists()) return;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(packageName + "." + file.getName(), file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
    }

    private static void scanJar(String packageName, JarFile jar, Set<Class<?>> classes) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                String className = name.replace('/', '.').replace(".class", "");
                classes.add(Class.forName(className));
            }
        }
    }
}
