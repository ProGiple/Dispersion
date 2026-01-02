package com.satellite.progiple.cache;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Getter
public class Cache<K, V> {
    private final int maxSize;
    private final long ttlMillis;
    private final LinkedHashMap<K, CacheEntry<V>> map;
    public Cache(int maxSize, long ttlMillis) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > Cache.this.maxSize;
            }
        };
    }

    public Cache(int maxSize, long ttl, TimeUnit ttlTimeUnit) {
        this(maxSize, ttlTimeUnit.toMillis(ttl));
    }

    public synchronized void put(K key, V value) {
        map.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public synchronized V get(K key, Supplier<V> orElse) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) return orElse.get();

        if (System.currentTimeMillis() - entry.timestamp() > ttlMillis) {
            this.remove(key);
            return orElse.get();
        }

        return entry.value();
    }

    public synchronized V get(K key) {
        return this.get(key, () -> null);
    }

    public synchronized void remove(K key) {
        map.remove(key);
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return "Cache{" + "maxSize=" + maxSize + ", ttlMillis=" + ttlMillis + '}';
    }
}
