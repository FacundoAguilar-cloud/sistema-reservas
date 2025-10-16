package com.payments.microservices.msvc_payments.security.services;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RateLimitService {

private static final int MAX_ATTEMPTS_PER_HOUR= 10;

private final LoadingCache <String, AtomicInteger> attemptsCache;

public RateLimitService() {
        this.attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(String key) {
                    return new AtomicInteger(0);
                }
            });
    }

     
 public boolean tryConsume(String clientIdentifier){
    try {
        AtomicInteger attempts = attemptsCache.get(clientIdentifier);
        int currentAttempts = attempts.incrementAndGet();

        if (currentAttempts > MAX_ATTEMPTS_PER_HOUR) {
            log.warn("Rate limit exceeded for", clientIdentifier);
            return false;
        }
        log.info("Rate limit passed ", currentAttempts, MAX_ATTEMPTS_PER_HOUR);
        return true; 

    } catch (ExecutionException e) {
        log.error("Error checking rate limit", e);
        return true;
    }
 }   
    
    public void reset(String clientIdentifier) {
        attemptsCache.invalidate(clientIdentifier);
    }


}


