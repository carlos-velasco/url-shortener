package com.velasconino;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.MediaType;
import com.velasconino.dto.UrlRequestDto;
import com.velasconino.dto.UrlResponseDto;
import java.util.UUID;

@Controller("/")
public class UrlShortenerController {

    private static final String BASE_SHORT_URL = "https://myshortener.com/";

    @Post(value = "/shorten", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<UrlResponseDto> createShortUrl(@Body UrlRequestDto request) {
        String shortCode = generateRandomShortCode();
        String shortUrl = BASE_SHORT_URL + shortCode;
        return HttpResponse.created(new UrlResponseDto(shortUrl));
    }

    @Get("/{shortCode}")
    public HttpResponse<?> redirectToOriginal(@PathVariable String shortCode) {
        // For now, redirect to a random URL as per requirements
        String randomUrl = "https://example.com/" + UUID.randomUUID().toString();
        return HttpResponse.redirect(java.net.URI.create(randomUrl));
    }

    private String generateRandomShortCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
} 