package com.velasconino.infrastructure.adapters.output;

import com.velasconino.application.ports.output.UrlRepository;

import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the UrlRepository.
 */
@Singleton
public class InMemoryUrlRepository implements UrlRepository {
    
    private final Map<String, String> urlMap = new ConcurrentHashMap<>();
    
    @Override
    public boolean existsByShortCode(String shortCode) {
        return urlMap.containsKey(shortCode);
    }
    
    @Override
    public void save(String shortCode, String originalUrl) {
        urlMap.put(shortCode, originalUrl);
    }
    
    @Override
    public Optional<String> findOriginalUrlByShortCode(String shortCode) {
        return Optional.ofNullable(urlMap.get(shortCode));
    }
    
    /**
     * Clears all entries from the repository.
     * This method is primarily used for testing.
     */
    public void clear() {
        urlMap.clear();
    }
} 