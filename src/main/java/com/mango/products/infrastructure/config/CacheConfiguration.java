package com.mango.products.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring cache configuration.
 */
@Configuration
public class CacheConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    /**
     * Cache configuration.
     * Uses ConcurrentMapCacheManager (synchronized HashMap).
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Cache Manager initialized: ConcurrentMapCacheManager (In-Memory)");
        log.info("Available caches: [products, productsByName]");

        return new ConcurrentMapCacheManager(
                "products",
                "productsByName"
        );
    }

}
