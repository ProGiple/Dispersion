package com.satellite.progiple.filles.handlers;

import com.satellite.progiple.utils.DiMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvStorage {
    private final Map<String, String> map = new HashMap<>();

    public void add(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public int getInt(String key) {
        return DiMath.toInt(get(key));
    }

    public double getDouble(String key) {
        return DiMath.toDouble(get(key));
    }

    public long getLong(String key) {
        return DiMath.toLong(get(key));
    }

    public float getFloat(String key) {
        return DiMath.toFloat(get(key));
    }

    public byte getByte(String key) {
        return DiMath.toByte(get(key));
    }

    public boolean getBoolean(String key) {
        return DiMath.toBoolean(get(key));
    }

    public Collection<String> getKeys(String prefix) {
        return prefix == null ? map.keySet() : map.keySet().stream().filter(s -> s.startsWith(prefix)).toList();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int length() {
        return map.size();
    }
}
