package com.velasconino.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Domain object representing a URL shortener that generates hash-based alphanumeric codes.
 * Uses SHA-256 hashing and Base64 encoding to create short codes consisting of
 * letters (a-z, A-Z) and numbers (0-9).
 */
@Getter
@RequiredArgsConstructor
public class HashBasedAlphanumericShortener {
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    private final String url;

    /**
     * Generates a short code for this URL with the specified length.
     * 
     * @param length The length of the short code to generate
     * @return A short code of the specified length consisting of alphanumeric characters
     */
    public String generateShortCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        try {
            // Create a hash of the URL
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(url.getBytes(StandardCharsets.UTF_8));
            
            // Convert to Base64 to get more characters
            String base64Hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
            
            // Filter to only include alphanumeric characters and ensure length
            StringBuilder shortCode = new StringBuilder();
            
            for (char c : base64Hash.toCharArray()) {
                if (ALPHANUMERIC_CHARS.indexOf(c) != -1) {
                    shortCode.append(c);
                    if (shortCode.length() == length) {
                        break;
                    }
                }
            }
            
            // If we don't have enough characters, append more deterministically
            while (shortCode.length() < length) {
                int index = (shortCode.length() * 31 + url.hashCode()) % ALPHANUMERIC_CHARS.length();
                if (index < 0) {
                    index += ALPHANUMERIC_CHARS.length();
                }
                shortCode.append(ALPHANUMERIC_CHARS.charAt(index));
            }
            
            return shortCode.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate short code", e);
        }
    }
} 