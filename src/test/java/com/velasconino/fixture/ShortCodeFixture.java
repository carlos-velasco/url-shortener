package com.velasconino.fixture;

import java.util.UUID;

/**
 * Fixture for generating test short codes.
 */
public class ShortCodeFixture {
    
    /**
     * Generates a unique short code for testing purposes.
     * 
     * @return A unique short code string
     */
    public static String aUniqueShortCode() {
        return "test-" + UUID.randomUUID().toString().substring(0, 8);
    }
} 