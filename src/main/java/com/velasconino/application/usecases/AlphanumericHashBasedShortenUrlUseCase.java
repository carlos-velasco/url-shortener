package com.velasconino.application.usecases;

import java.util.Optional;

import com.velasconino.application.ports.input.ShortenUrlCommand;
import com.velasconino.application.ports.input.ShortenUrlUseCase;
import com.velasconino.application.ports.input.UrlShortenedResponse;
import com.velasconino.application.ports.output.UrlRepository;
import com.velasconino.domain.HashBasedAlphanumericShortener;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

/**
 * Implementation of the ShortenUrlUseCase that generates alphanumeric codes
 * based on SHA-256 hash of the URL. The generated short codes consist only of
 * letters (a-z, A-Z) and numbers (0-9).
 */
@Singleton
public class AlphanumericHashBasedShortenUrlUseCase implements ShortenUrlUseCase {
    
    private final int initialCodeLength;
    private final String baseShortUrl;
    private final UrlRepository urlRepository;
    
    public AlphanumericHashBasedShortenUrlUseCase(
            UrlRepository urlRepository,
            @Value("${url.shortener.initial-code-length}") int initialCodeLength,
            @Value("${url.shortener.base-url}") String baseShortUrl) {
        this.urlRepository = urlRepository;
        this.initialCodeLength = initialCodeLength;
        this.baseShortUrl = baseShortUrl;
    }
    
    @Override
    public UrlShortenedResponse shortenUrl(ShortenUrlCommand command) {
        HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener(command.url());
        
        // Find or generate a unique short code
        String shortCode = findOrGenerateUniqueShortCode(shortener, command.url());
        
        return UrlShortenedResponse.of(shortCode, baseShortUrl);
    }
    
    /**
     * Finds an existing short code for the URL or generates a new unique one.
     * If a new code is generated, it is saved in the repository.
     * 
     * @param shortener The shortener to use for code generation
     * @param url The URL to find or generate a code for
     * @return A short code for the URL
     */
    private String findOrGenerateUniqueShortCode(HashBasedAlphanumericShortener shortener, String url) {
        int codeLength = initialCodeLength;
        
        while (true) {
            String shortCode = shortener.generateShortCode(codeLength);
            Optional<String> existingUrl = urlRepository.findOriginalUrlByShortCode(shortCode);
            
            if (existingUrl.isEmpty()) {
                // New short code, save and return it
                urlRepository.save(shortCode, url);
                return shortCode;
            }
            
            if (existingUrl.get().equals(url)) {
                // URL already has this short code, return it
                return shortCode;
            }
            
            // Code collision with different URL, try longer code
            codeLength++;
        }
    }
} 