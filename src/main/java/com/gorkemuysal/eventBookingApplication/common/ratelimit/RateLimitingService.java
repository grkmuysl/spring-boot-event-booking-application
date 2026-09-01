package com.gorkemuysal.eventBookingApplication.common.ratelimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // resolves bucket according to IP or userID endpoint
    public Bucket resolveBucket(String key, PlanType planType) {
        return cache.computeIfAbsent(key, k -> createNewBucket(planType));
    }

    private Bucket createNewBucket(PlanType planType) {
        return switch (planType) {
            case AUTH_LOGIN -> Bucket.builder()
                    // 5 attempts per minute
                    .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                    .build();
            case RESERVATION -> Bucket.builder()
                    // 10 reservation attemps per mimiute
                    .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                    .build();
            case GENERAL_PUBLIC -> Bucket.builder()
                    // 60 get api calls attempts per minute
                    .addLimit(Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))))
                    .build();
        };
    }

}
