package com.velasconino.application.ports.input;

/**
 * Response object containing the result of a URL shortening operation.
 */
public record UrlShortenedResponse(String shortCode, String shortUrl) {
    
    public static UrlShortenedResponse of(String shortCode, String baseUrl) {
        return new UrlShortenedResponse(shortCode, baseUrl + shortCode);
    }
} 