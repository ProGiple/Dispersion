package com.satellite.progiple.cache;

import com.satellite.progiple.utils.Utilities;

public record CacheEntry<V>(V value, long timestamp) {
    @Override
    public String toString() {
        return Utilities.toString(this);
    }
}
