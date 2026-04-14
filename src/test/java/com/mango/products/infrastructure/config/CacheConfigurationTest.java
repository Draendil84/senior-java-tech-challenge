package com.mango.products.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CacheConfiguration.
 */
class CacheConfigurationTest {

    private final CacheConfiguration cacheConfiguration = new CacheConfiguration();

    @Test
    void shouldCreateConcurrentMapCacheManager() {
        CacheManager cacheManager = cacheConfiguration.cacheManager();

        assertNotNull(cacheManager);
        assertInstanceOf(ConcurrentMapCacheManager.class, cacheManager);
    }

    @Test
    void shouldContainExpectedCaches() {
        CacheManager cacheManager = cacheConfiguration.cacheManager();

        Cache productsCache = cacheManager.getCache("products");
        Cache productsByNameCache = cacheManager.getCache("productsByName");

        assertNotNull(productsCache);
        assertNotNull(productsByNameCache);
    }

    @Test
    void shouldStoreAndRetrieveValuesFromCache() {
        CacheManager cacheManager = cacheConfiguration.cacheManager();

        Cache cache = cacheManager.getCache("products");
        assertNotNull(cache);

        cache.put(1L, "test-product");

        Cache.ValueWrapper value = cache.get(1L);

        assertNotNull(value);
        assertEquals("test-product", value.get());
    }

    @Test
    void shouldHandleNullCacheGracefully() {
        CacheManager cacheManager = cacheConfiguration.cacheManager();

        Cache cache = cacheManager.getCache("nonExisting");

        assertNull(cache);
    }

}
