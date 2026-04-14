package com.mango.products.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring cache configuration.
 */
@Slf4j
@Configuration
public class CacheConfiguration {

    /**
     * Cache configuration.
     * Uses ConcurrentMapCacheManager (synchronized HashMap).
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Cache Manager initialized: ConcurrentMapCacheManager (In-Memory)");
        log.info("Available caches: [products, productsByName]");

        return new ConcurrentMapCacheManager(
                "products",         // findById(Long id)
                "productsByName"                // findByName(String name)
        );
    }

}
