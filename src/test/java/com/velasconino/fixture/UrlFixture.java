package com.velasconino.fixture;

import java.util.UUID;

/**
 * Fixture for generating test URLs.
 */
public class UrlFixture {
    
    /**
     * Generates a unique URL for testing purposes.
     * 
     * @return A unique URL string
     */
    public static String aUniqueUrl() {
        return "https://example.com/" + UUID.randomUUID().toString();
    }
} 