package com.velasconino.application.ports.output;

import java.util.Optional;

/**
 * Repository interface for URL shortening operations.
 */
public interface UrlRepository {
    
    /**
     * Checks if a short code already exists in the repository.
     * 
     * @param shortCode The short code to check
     * @return true if the short code exists, false otherwise
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * Saves a mapping between a short code and its original URL.
     * 
     * @param shortCode The short code
     * @param originalUrl The original URL
     */
    void save(String shortCode, String originalUrl);
    
    /**
     * Finds the original URL for a given short code.
     * 
     * @param shortCode The short code to look up
     * @return An Optional containing the original URL if found, or empty if not found
     */
    Optional<String> findOriginalUrlByShortCode(String shortCode);
} 