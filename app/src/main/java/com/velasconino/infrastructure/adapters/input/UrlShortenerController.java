package com.velasconino.infrastructure.adapters.input;

import com.velasconino.application.ports.input.ResolveShortUrlUseCase;
import com.velasconino.application.ports.input.ShortCodeQuery;
import com.velasconino.application.ports.input.ShortenUrlCommand;
import com.velasconino.application.ports.input.ShortenUrlUseCase;
import com.velasconino.application.ports.input.UrlShortenedResponse;
import com.velasconino.infrastructure.adapters.input.dto.UrlRequestDto;
import com.velasconino.infrastructure.adapters.input.dto.UrlResponseDto;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.MediaType;

import java.net.URI;

/**
 * REST controller for URL shortening operations.
 */
@Controller("/")
public class UrlShortenerController {
    
    private final ShortenUrlUseCase shortenUrlUseCase;
    private final ResolveShortUrlUseCase resolveShortUrlUseCase;
    
    public UrlShortenerController(ShortenUrlUseCase shortenUrlUseCase, 
                                 ResolveShortUrlUseCase resolveShortUrlUseCase) {
        this.shortenUrlUseCase = shortenUrlUseCase;
        this.resolveShortUrlUseCase = resolveShortUrlUseCase;
    }
    
    @Post(value = "/shorten", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<UrlResponseDto> createShortUrl(@Body UrlRequestDto request) {
        UrlShortenedResponse response = shortenUrlUseCase.shortenUrl(
                new ShortenUrlCommand(request.url()));
        
        return HttpResponse.created(new UrlResponseDto(response.shortUrl()));
    }
    
    @Get("/{shortCode}")
    public HttpResponse<?> redirectToOriginal(@PathVariable String shortCode) {
        // Create a ShortCodeQuery object that will validate the short code
        ShortCodeQuery query = new ShortCodeQuery(shortCode);
        
        return resolveShortUrlUseCase.resolveShortUrl(query)
                .map(originalUrl -> HttpResponse.redirect(URI.create(originalUrl)))
                .orElse(HttpResponse.notFound());
    }
} 