package com.velasconino.infrastructure.adapters.input;

import com.velasconino.application.exceptions.UrlShorteningCollisionException;
import com.velasconino.application.ports.input.ShortenUrlUseCase;
import com.velasconino.infrastructure.adapters.input.dto.UrlRequestDto;
import com.velasconino.infrastructure.adapters.input.dto.UrlResponseDto;
import com.velasconino.infrastructure.adapters.input.error.UrlShortenerExceptionHandler.ErrorResponse;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.velasconino.fixture.UrlFixture.aUniqueUrl;

/**
 * Test class for URL shortening collision scenarios.
 * Uses a mock ShortenUrlUseCase that always throws UrlShorteningCollisionException.
 */
@MicronautTest
class UrlShortenerControllerCollisionTest {

    @Inject
    @Client("/")
    HttpClient client;

    @MockBean(ShortenUrlUseCase.class)
    ShortenUrlUseCase shortenUrlUseCase() {
        return command -> {
            throw new UrlShorteningCollisionException(
                "Could not generate a unique short code within the maximum allowed length increase");
        };
    }

    @Test
    @DisplayName("Should return 500 when URL shortening collision occurs")
    void testUrlShorteningCollisionReturnsServerError() {
        // Given
        String originalUrl = aUniqueUrl();
        var request = new UrlRequestDto(originalUrl);
        
        // When/Then
        HttpClientResponseException exception = assertThrows(
            HttpClientResponseException.class,
            () -> client.toBlocking().exchange(
                HttpRequest.POST("/shorten", request),
                UrlResponseDto.class
            )
        );
        
        // Then
        assertThat(exception.getStatus().getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
        
        // Parse the error response
        ErrorResponse errorResponse = exception.getResponse().getBody(ErrorResponse.class).get();
        
        // Verify the error response
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
        assertThat(errorResponse.message()).isEqualTo("URL shortening collision occurred");
    }
} 