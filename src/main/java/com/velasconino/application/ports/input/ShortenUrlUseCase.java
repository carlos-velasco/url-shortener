package com.velasconino.application.ports.input;

/**
 * Use case interface for shortening URLs.
 */
public interface ShortenUrlUseCase {
    
    /**
     * Shortens a URL based on the provided command.
     * 
     * @param command The command containing the URL to shorten
     * @return The response containing the short code and full short URL
     */
    UrlShortenedResponse shortenUrl(ShortenUrlCommand command);
} 