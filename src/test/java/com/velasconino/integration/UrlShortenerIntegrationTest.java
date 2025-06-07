package com.velasconino.integration;

import com.velasconino.infrastructure.adapters.input.dto.UrlRequestDto;
import com.velasconino.infrastructure.adapters.input.dto.UrlResponseDto;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static com.velasconino.fixture.UrlFixture.aUniqueUrl;

/**
 * Integration test for the URL shortener application.
 * Tests the entire flow from creating a short URL to redirecting to the original URL.
 */
@MicronautTest
public class UrlShortenerIntegrationTest {
    
    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    DefaultHttpClientConfiguration configuration;
        
    @Value("${url.shortener.base-url}")
    private String baseShortUrl;
    
    @BeforeEach
    void setUp() {
        configuration.setFollowRedirects(false);
    }
    
    @Test
    void testFullUrlShorteningFlow() {
        // Given
        String originalUrl = aUniqueUrl();
        var request = new UrlRequestDto(originalUrl);
        
        // When - Create a short URL
        var createResponse = client.toBlocking()
                .exchange(HttpRequest.POST("/shorten", request), 
                    UrlResponseDto.class);
        
        // Then - Verify the short URL was created
        assertThat(createResponse.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        String shortUrl = createResponse.body().shortUrl();
        assertThat(shortUrl).startsWith(baseShortUrl);
        
        // Extract the short code from the URL
        String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
        
        // When - Use the short code to redirect
        var redirectResponse = client.toBlocking()
                .exchange(HttpRequest.GET("/" + shortCode));
        
        // Then - Verify the redirect
        assertThat(redirectResponse.status().getCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.getCode());
        String redirectUrl = redirectResponse.getHeaders().get("Location");
        assertThat(redirectUrl).isEqualTo(originalUrl);
    }
    
    @Test
    void testMultipleUrlsAreIndependent() {
        // Given
        String url1 = aUniqueUrl();
        String url2 = aUniqueUrl();
        
        // When - Create two short URLs
        var response1 = client.toBlocking()
                .exchange(HttpRequest.POST("/shorten", new UrlRequestDto(url1)), 
                    UrlResponseDto.class);
        
        var response2 = client.toBlocking()
                .exchange(HttpRequest.POST("/shorten", new UrlRequestDto(url2)), 
                    UrlResponseDto.class);
        
        // Then - Extract short codes
        String shortCode1 = response1.body().shortUrl().substring(response1.body().shortUrl().lastIndexOf('/') + 1);
        String shortCode2 = response2.body().shortUrl().substring(response2.body().shortUrl().lastIndexOf('/') + 1);
        
        // Verify codes are different
        assertThat(shortCode1).isNotEqualTo(shortCode2);
        
        // When - Use both short codes
        var redirect1 = client.toBlocking().exchange(HttpRequest.GET("/" + shortCode1));
        var redirect2 = client.toBlocking().exchange(HttpRequest.GET("/" + shortCode2));
        
        // Then - Verify correct redirects
        assertThat(redirect1.getHeaders().get("Location")).isEqualTo(url1);
        assertThat(redirect2.getHeaders().get("Location")).isEqualTo(url2);
    }
} 