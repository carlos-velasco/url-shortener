package com.velasconino.infrastructure.adapters.input;

import com.velasconino.application.ports.output.UrlRepository;
import com.velasconino.infrastructure.adapters.input.dto.UrlRequestDto;
import com.velasconino.infrastructure.adapters.input.dto.UrlResponseDto;
import com.velasconino.infrastructure.adapters.input.error.UrlShortenerExceptionHandler.ErrorResponse;
import com.velasconino.infrastructure.adapters.output.InMemoryUrlRepository;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static com.velasconino.fixture.ShortCodeFixture.aUniqueShortCode;

@MicronautTest
class UrlShortenerControllerTest {

    @Inject
    @Client("/")
    HttpClient client;
    
    @Inject
    DefaultHttpClientConfiguration configuration;
    
    @Inject
    UrlRepository urlRepository;
    
    @Value("${url.shortener.base-url}")
    private String baseShortUrl;
    
    @MockBean(UrlRepository.class)
    UrlRepository urlRepository() {
        return new InMemoryUrlRepository();
    }

    @BeforeEach
    void setUp() {
        configuration.setFollowRedirects(false);
 
    }

    @Nested
    @DisplayName("POST /shorten endpoint")
    class ShortenUrlTests {
        
        @Test
        @DisplayName("Should create a short URL successfully")
        void testCreateShortUrl() {
            // Given
            String originalUrl = aUniqueUrl();
            var request = new UrlRequestDto(originalUrl);

            // When
            var response = client.toBlocking()
                    .exchange(HttpRequest.POST("/shorten", request), 
                        UrlResponseDto.class);

            // Then
            assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
            assertThat(response.body().shortUrl()).startsWith(baseShortUrl);
            
            // Extract the short code from the URL
            String shortUrl = response.body().shortUrl();
            String shortCode = shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
            
            // Verify the mapping exists in the repository
            assertThat(urlRepository.findOriginalUrlByShortCode(shortCode))
                .isPresent()
                .hasValueSatisfying(url -> assertThat(url).isEqualTo(originalUrl));
        }
        
        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return 400 when URL is null or empty")
        void testEmptyUrlReturnsBadRequest(String emptyUrl) {
            // Given
            var request = new UrlRequestDto(emptyUrl);
            
            // When/Then
            HttpClientResponseException exception = assertThrowsBadRequestWithErrorResponse(
                request, "URL cannot be null or empty");
            
            assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
        
        @ParameterizedTest
        @ValueSource(strings = {
            "not-a-valid-url",
            "http://",
            "ftp://",
            "example.com",
            "www.example.com"
        })
        @DisplayName("Should return 400 when URL format is invalid")
        void testInvalidUrlFormatReturnsBadRequest(String invalidUrl) {
            // Given
            var request = new UrlRequestDto(invalidUrl);
            
            // When/Then
            HttpClientResponseException exception = assertThrowsBadRequestWithErrorResponse(
                request, "Invalid URL format");
            
            assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
    }
    
    @Nested
    @DisplayName("GET /{shortCode} endpoint")
    class RedirectTests {
        
        @Test
        @DisplayName("Should redirect to the original URL")
        void testRedirectToOriginal() {
            // Given - pre-populate the repository with a mapping
            String shortCode = aUniqueShortCode();
            String originalUrl = aUniqueUrl();
            urlRepository.save(shortCode, originalUrl);

            // When
            var response = client.toBlocking()
                    .exchange(HttpRequest.GET("/" + shortCode));

            // Then
            assertThat(response.status().getCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.getCode());
            assertThat(response.getHeaders().get("Location")).isEqualTo(originalUrl);
        }
        
        @Test
        @DisplayName("Should return 404 when short code doesn't exist")
        void testRedirectNotFound() {
            // Given - a short code that doesn't exist in the repository
            String shortCode = aUniqueShortCode();

            // When/Then
            HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/" + shortCode))
            );
            
            // Then
            assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        }
        
        @Test
        @DisplayName("Should return 400 when short code is empty")
        void testEmptyShortCodeReturnsBadRequest() {
            // When/Then
            HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/%20"))
            );
            
            // Then
            assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            
            // Parse the error response
            ErrorResponse errorResponse = exception.getResponse().getBody(ErrorResponse.class).get();
            
            // Verify the error response
            assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            assertThat(errorResponse.message()).contains("Short code cannot be null or empty");
        }
    }
    
    /**
     * Helper method to assert that a bad request exception is thrown with the expected error message.
     * 
     * @param request The request DTO
     * @param expectedErrorMessagePart Part of the expected error message
     * @return The exception for further assertions
     */
    private HttpClientResponseException assertThrowsBadRequestWithErrorResponse(
            UrlRequestDto request, String expectedErrorMessagePart) {
        
        // Assert that the exception is thrown
        HttpClientResponseException exception = org.junit.jupiter.api.Assertions.assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(
                HttpRequest.POST("/shorten", request),
                UrlResponseDto.class
            )
        );
        
        // Parse the error response
        ErrorResponse errorResponse = exception.getResponse().getBody(ErrorResponse.class).get();
        
        // Verify the error response
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        assertThat(errorResponse.message()).contains(expectedErrorMessagePart);
        
        return exception;
    }
} 