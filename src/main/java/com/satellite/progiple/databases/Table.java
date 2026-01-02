package com.satellite.progiple.databases;

import com.satellite.progiple.databases.reflection.*;
import com.satellite.progiple.exceptions.TargetFieldNameExistsNowException;
import com.satellite.progiple.utils.Utilities;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.*;

public class Table<S extends ITableStructure> implements ITable<S> {
    public final String tableName;
    @Getter private final boolean async;

    protected final AsyncExecutor database;
    protected final Class<S> structure;
    protected final Map<String, Field> loadedFields; // { ИМЯ В ТАБЛИЦЕ , ПОЛЕ В СТРУКТУРЕ }
    protected final boolean allFieldsAreUpdatable;
    public Table(AsyncExecutor database, Class<S> structure, String tableName, boolean async) {
        this.tableName = tableName;
        this.async = async;

        this.structure = structure;
        this.database = database;
        this.loadedFields = new HashMap<>();
        this.loadFields(this.structure);

        TableProcessing tableProcessing = structure.getAnnotation(TableProcessing.class);
        this.allFieldsAreUpdatable = tableProcessing != null && tableProcessing.updatable();
    }

    private void loadFields(Class<? extends ITableStructure> clazz) {
        boolean classFullProcessing = clazz.isAnnotationPresent(TableProcessing.class);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            TableProcessing fieldProcessing = field.getAnnotation(TableProcessing.class);

            String dbFieldName = fieldProcessing == null || fieldProcessing.name().isEmpty() ? field.getName() : fieldProcessing.name();
            if (loadedFields.containsKey(dbFieldName)) throw new TargetFieldNameExistsNowException(dbFieldName, tableName);

            if ((classFullProcessing || fieldProcessing != null) && Variant.get(field) != null) {
                this.loadedFields.put(dbFieldName, field);
            }
        }
    }

    @Override
    public void loadTable() {
        StringBuilder builder = new StringBuilder();
        this.loadedFields.forEach((key, f) -> {
            if (!builder.isEmpty()) builder.append(", ");
            builder.append(key).append(" ");

            Variant variant = Variant.get(f);
            builder.append(variant == Variant.ENUM ? "VARCHAR(88)" : variant.name());

            if (variant == Variant.VARCHAR) {
                int size = Variant.getVarchar(f);
                builder.append("(").append(size).append(")");
            }

            if (f.isAnnotationPresent(PrimaryKey.class)) {
                builder.append(" NOT NULL");
                if (f.isAnnotationPresent(AutoIncrement.class)) {
                    builder.append(" AUTO_INCREMENT");
                }
                builder.append(" PRIMARY KEY");
            }
            else {
                if (f.isAnnotationPresent(NotNull.class)) {
                    builder.append(" NOT NULL");
                }

                if (f.isAnnotationPresent(Unique.class)) {
                    builder.append(" UNIQUE");
                }
            }
        });

        this.database.executeSync(String.format("CREATE TABLE IF NOT EXISTS `%s` (%s);", this.tableName, builder.toString()));
    }

    @Override @SneakyThrows
    public void insert(S structure, boolean async) {
        StringBuilder fieldNames = new StringBuilder();
        List<Object> objects = new ArrayList<>();

        for (Map.Entry<String, Field> entry : this.loadedFields.entrySet()) {
            Object value = entry.getValue().get(structure);
            if (value == null && entry.getValue().isAnnotationPresent(AutoIncrement.class)) continue;

            if (!fieldNames.isEmpty()) fieldNames.append(", ");

            fieldNames.append(entry.getKey());
            objects.add(value instanceof Enum<?> e ? e.name() : value);
        }

        int size = objects.size();
        String query = String.format("INSERT INTO `%s` (%s) VALUES (%s);",
                this.tableName,
                fieldNames.toString(),
                "?, ".repeat(size).substring(0, size * 3 - 2));

        this.request(query, async, objects.toArray());
        structure.onInsert(this);
    }

    @Override @SneakyThrows
    public void update(S structure, String whereCondition, boolean async) {
        StringBuilder setQuery = new StringBuilder();
        List<Object> objects = new ArrayList<>();

        for (Map.Entry<String, Field> entry : this.loadedFields.entrySet()) {
            if (!this.allFieldsAreUpdatable) {
                TableProcessing annotation = entry.getValue().getAnnotation(TableProcessing.class);
                if (annotation == null || !annotation.updatable()) continue;
            }

            Object value = entry.getValue().get(structure);
            if (value == null && entry.getValue().isAnnotationPresent(AutoIncrement.class)) continue;

            if (!setQuery.isEmpty()) setQuery.append(", ");
            setQuery.append(entry.getKey()).append(" = ?");
            objects.add(value instanceof Enum<?> e ? e.name() : value);
        }

        // SET pole1 = value1, pole2 = value2

        whereCondition = whereCondition == null ? "" : " WHERE " + whereCondition;
        String query = String.format("UPDATE `%s` SET %s%s;", this.tableName, setQuery.toString(), whereCondition);
        System.out.println(query);

        this.request(query, async, objects);
        structure.onUpdate(this);
    }

    @Override
    public void delete(String whereCondition, boolean async) {
        whereCondition = whereCondition == null ? "" : "WHERE " + whereCondition;
        String query = String.format("DELETE FROM `%s`%s;", this.tableName, whereCondition);
        this.request(query, async);
    }

    @Override
    public void request(String query, boolean async, Object... objects) {
        if (async) this.database.executeAsync(query, objects);
        else this.database.executeSync(query, objects);
    }

    @Override
    public <E> List<E> get(String whereCondition, ResultSetHandler<E> handler) {
        String query = String.format("SELECT * FROM `%s` WHERE %s;", this.tableName, whereCondition);
        return this.database.executeQuery(query, handler);
    }

    @Override @SneakyThrows
    public List<S> get(String whereCondition) {
        List<S> list = new ArrayList<>();

        String query = String.format("SELECT * FROM `%s` WHERE %s;", this.tableName, whereCondition);
        this.database.executeQuery(query, rs -> {
            do {
                S obj = this.structure.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Field> entry : this.loadedFields.entrySet()) {
                    String column = entry.getKey();
                    Field field = entry.getValue();

                    Object value = rs.getObject(column);
                    if (field.getType().isEnum() && value instanceof String str) {
                        @SuppressWarnings("unchecked")
                        Object enumValue = Enum.valueOf((Class<? extends Enum>) field.getType(), str);
                        field.set(obj, enumValue);
                    } else {
                        field.set(obj, value);
                    }
                }

                list.add(obj);
            } while (rs.next());
            return null;
        });

        return list;
    }

    @Override
    public void clear() {
        this.database.executeAsync("TRUNCATE TABLE " + this.tableName + ";");
    }

    @Override
    public void drop() {
        this.database.executeSync("DROP TABLE " + this.tableName + ";");
    }

    @Override
    public String toString() {
        return Utilities.toString(this);
    }
}
