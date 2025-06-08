package com.velasconino.application.ports.input;

import com.velasconino.application.exceptions.EmptyUrlException;
import com.velasconino.application.exceptions.InvalidUrlException;

import java.util.regex.Pattern;

/**
 * Command object for the ShortenUrlUseCase.
 */
public record ShortenUrlCommand(String url) {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    
    public ShortenUrlCommand {
        if (url == null || url.trim().isEmpty()) {
            throw new EmptyUrlException("URL cannot be null or empty");
        }
        
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new InvalidUrlException("Invalid URL format: " + url);
        }
    }
} 