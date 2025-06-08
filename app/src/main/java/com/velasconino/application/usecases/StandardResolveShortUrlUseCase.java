package com.velasconino.application.usecases;

import com.velasconino.application.ports.input.ResolveShortUrlUseCase;
import com.velasconino.application.ports.input.ShortCodeQuery;
import com.velasconino.application.ports.output.UrlRepository;

import jakarta.inject.Singleton;
import java.util.Optional;

/**
 * Standard implementation of the ResolveShortUrlUseCase.
 * Uses the repository to look up the original URL by short code.
 */
@Singleton
public class StandardResolveShortUrlUseCase implements ResolveShortUrlUseCase {
    
    private final UrlRepository urlRepository;
    
    public StandardResolveShortUrlUseCase(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    
    @Override
    public Optional<String> resolveShortUrl(ShortCodeQuery query) {
        // The validation is already done in the ShortCodeQuery constructor
        return urlRepository.findOriginalUrlByShortCode(query.shortCode());
    }
} 