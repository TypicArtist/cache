package cache;

import org.junit.jupiter.api.Test;

import net.typicartist.cache.Cache;
import net.typicartist.cache.ICache;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    @Test
    void testPutAndGet() {
        ICache<String, String> cache = new Cache<>(10, 1000); // 1秒 TTL

        cache.put("key", "value");
        String result = cache.get("key");

        assertEquals("value", result);
    }

    @Test
    void testExpiration() throws InterruptedException {
        ICache<String, String> cache = new Cache<>(10, 100); // 100ms TTL

        cache.put("key", "value");
        Thread.sleep(150); // expireさせる
        String result = cache.get("key");

        assertNull(result); // 期限切れでnullになるはず
    }

    @Test
    void testRemove() {
        ICache<String, String> cache = new Cache<>(10, 1000);

        cache.put("key", "value");
        cache.remove("key");
        assertNull(cache.get("key"));
    }

    @Test
    void testClear() {
        ICache<String, String> cache = new Cache<>(10, 1000);

        cache.put("a", "1");
        cache.put("b", "2");

        cache.clear();
        assertNull(cache.get("a"));
        assertNull(cache.get("b"));
    }

    @Test
    void testMaxSizeEviction() {
        ICache<Integer, String> cache = new Cache<>(3, 1000);

        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        cache.put(4, "four"); // 1番最初のエントリ（1）が消えるはず

        assertNull(cache.get(1)); // LRUで削除される
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
        assertEquals("four", cache.get(4));
    }
}