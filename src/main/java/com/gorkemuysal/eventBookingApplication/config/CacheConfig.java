package com.gorkemuysal.eventBookingApplication.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * A configuration class to handle chache mechanism
 * */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String EVENTS_CACHE = "events";
    public static final String ALL_EVENTS_CACHE = "allEvents";

    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(EVENTS_CACHE, ALL_EVENTS_CACHE);
        cacheManager.setCaffeine(defaultCaffeineConfig());
        return cacheManager;
    }

    private Caffeine<Object, Object> defaultCaffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats(); 
    }
}