package com.satellite.progiple.registration;

import com.satellite.progiple.databases.AsyncExecutor;
import com.satellite.progiple.databases.DataBaseParams;
import com.satellite.progiple.databases.ITable;
import com.satellite.progiple.databases.reflection.*;
import com.satellite.progiple.filles.DiFiles;
import com.satellite.progiple.filles.reflection.EnvData;
import com.satellite.progiple.filles.reflection.ScriptData;
import com.satellite.progiple.scanner.DiScanner;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.*;
import java.util.*;

@UtilityClass
public class DiRegister {
    @SneakyThrows
    public void initialize(String packageName) {
        Set<Class<?>> classes = DiScanner.scanPackage(packageName);
        callMethods(classes, EventType.ON_START);

        classes.forEach(DiRegister::regFiles);
        callMethods(classes, EventType.REG_FILES);

        classes.forEach(DiRegister::regAutowired);
        callMethods(classes, EventType.REG_AUTOWIRED);

        classes.forEach(DiRegister::regDatabase);
        callMethods(classes, EventType.REG_TABLES);

        classes.forEach(DiRegister::regScripts);
        callMethods(classes, EventType.REG_SCRIPTS);

        callMethods(classes, EventType.AFTER_INIT);
    }

    public void initialize(Class<?> clazz) {
        initialize(clazz.getPackage().getName());
    }

    private void callMethods(Collection<Class<?>> classes, EventType eventType) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        for (Class<?> c : classes) {
            Arrays.stream(c.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Event.class) &&
                            m.getAnnotation(Event.class).value().equals(eventType))
                    .forEach(m -> {
                        m.setAccessible(true);
                        try {
                            m.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @SneakyThrows
    public void regDatabase(Class<?> clazz) {
        Set<Field> fields = new HashSet<>(List.of(clazz.getDeclaredFields()));

        Field databaseField = fields.stream()
                .filter(f -> f.isAnnotationPresent(DataBase.class))
                .findFirst()
                .orElse(null);

        AsyncExecutor asyncExecutor;
        if (databaseField == null) {
            databaseField = fields.stream()
                    .filter(f -> f.isAnnotationPresent(ConfDataBase.class))
                    .findFirst()
                    .orElse(null);
            if (databaseField == null) return;

            databaseField.setAccessible(true);
            ConfDataBase confDataBase = databaseField.getAnnotation(ConfDataBase.class);

            Class<?> parentClass = confDataBase.parentClass() == Void.class ? clazz : confDataBase.parentClass();
            Field field = parentClass.getDeclaredField(confDataBase.paramsField());
            field.setAccessible(true);

            DataBaseParams params = (DataBaseParams) field.get(null);
            asyncExecutor = new AsyncExecutor(params);
        }
        else {
            databaseField.setAccessible(true);
            DataBase dataBase = databaseField.getAnnotation(DataBase.class);

            asyncExecutor = new AsyncExecutor(
                    dataBase.driverClass(),
                    dataBase.url(),
                    dataBase.user(),
                    dataBase.password(),
                    dataBase.maxRetries(),
                    dataBase.poolSize(),
                    dataBase.queryTimeout(),
                    dataBase.validationTimeout(),
                    dataBase.asyncThreadPoolSize()
            );
        }

        databaseField.set(null, asyncExecutor);

        for (Field field : fields) {
            TableBase tableBase = field.getAnnotation(TableBase.class);
            ConfTableBase confTableBase = field.getAnnotation(ConfTableBase.class);
            if (tableBase != null || confTableBase != null) {
                field.setAccessible(true);
                Constructor<?> constructor = field.getType().getConstructor(
                        AsyncExecutor.class,
                        Class.class,
                        String.class,
                        boolean.class
                );

                Type genericType = field.getGenericType();
                if (!(genericType instanceof ParameterizedType pType)) continue;

                Class<?> actualType = (Class<?>) pType.getActualTypeArguments()[0];

                String tableName;
                boolean isAsync;
                if (tableBase != null) {
                    tableName = tableBase.name();
                    isAsync = tableBase.isAsync();
                }
                else {
                    Class<?> parentClass = confTableBase.parentClass() == Void.class ? clazz : confTableBase.parentClass();

                    Field confField = parentClass.getDeclaredField(confTableBase.tableNameField());
                    confField.setAccessible(true);
                    tableName = (String) confField.get(null);

                    confField = parentClass.getDeclaredField(confTableBase.tableAsyncField());
                    confField.setAccessible(true);
                    isAsync = (Boolean) confField.get(null);
                }

                ITable<?> table = (ITable<?>) constructor.newInstance(asyncExecutor, actualType, tableName, isAsync);
                field.set(null, table);
                table.loadTable();
            }
        }
    }

    @SneakyThrows
    public void regFiles(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            EnvData envData = field.getAnnotation(EnvData.class);
            if (envData != null) {
                field.setAccessible(true);
                field.set(null, DiFiles.loadEnv(envData.value()));
            }
        }
    }

    @SneakyThrows
    public void regScripts(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            ScriptData data = field.getAnnotation(ScriptData.class);
            if (data != null) {
                field.setAccessible(true);
                field.set(null, DiFiles.loadScript(data.packageName(), data.path()));
            }
        }
    }

    @SneakyThrows
    public void regAutowired(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);

                Class<?> type = field.getType();
                field.set(null, type.getDeclaredConstructor().newInstance());
            }
        }
    }
}
