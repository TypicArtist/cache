package cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> implements ICache<K, V> {
    private final long defaultTtl;
    private final int maxSize;

    private final Map<K, CacheEntry<V>> cache;

    public Cache(int maxSize, long defaultTtlMillis) {
        this.defaultTtl = defaultTtlMillis;
        this.maxSize = maxSize;

        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > Cache.this.maxSize;
            }
        };
    }

    @Override
    public synchronized V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    @Override
    public synchronized void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, defaultTtl));
    }

    @Override
    public synchronized void remove(K key) {
        cache.remove(key);
    }

    @Override
    public synchronized void clear() {
        cache.clear();
    }

    private static class CacheEntry<V> {
        final V value;
        final long expireAt;

        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expireAt = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }
}