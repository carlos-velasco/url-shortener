package com.velasconino.application.usecases;

import com.velasconino.application.exceptions.InvalidShortCodeException;
import com.velasconino.application.ports.input.ShortCodeQuery;
import com.velasconino.infrastructure.adapters.output.InMemoryUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ResolveShortUrlUseCase Implementation")
class StandardResolveShortUrlUseCaseTest {

    private InMemoryUrlRepository urlRepository;
    private StandardResolveShortUrlUseCase useCase;

    @BeforeEach
    void setUp() {
        urlRepository = new InMemoryUrlRepository();
        useCase = new StandardResolveShortUrlUseCase(urlRepository);
    }

    @Nested
    @DisplayName("URL resolution tests")
    class UrlResolutionTests {
        
        @Test
        @DisplayName("Should return original URL when short code exists")
        void shouldReturnOriginalUrlWhenShortCodeExists() {
            // Given
            String shortCode = "abc12345";
            String originalUrl = "https://example.com/path";
            urlRepository.save(shortCode, originalUrl);

            // When
            Optional<String> result = useCase.resolveShortUrl(new ShortCodeQuery(shortCode));

            // Then
            assertThat(result)
                .isPresent()
                .contains(originalUrl);
        }

        @Test
        @DisplayName("Should return empty when short code does not exist")
        void shouldReturnEmptyWhenShortCodeDoesNotExist() {
            // Given
            String shortCode = "nonexistent";

            // When
            Optional<String> result = useCase.resolveShortUrl(new ShortCodeQuery(shortCode));

            // Then
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("ShortCodeQuery validation tests")
    class ValidationTests {
        
        @Test
        @DisplayName("Should throw exception when short code is null")
        void shouldThrowExceptionWhenShortCodeIsNull() {
            // Then
            assertThatThrownBy(() -> new ShortCodeQuery(null))
                .isInstanceOf(InvalidShortCodeException.class)
                .hasMessageContaining("Short code cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when short code is empty")
        void shouldThrowExceptionWhenShortCodeIsEmpty() {
            // Then
            assertThatThrownBy(() -> new ShortCodeQuery(""))
                .isInstanceOf(InvalidShortCodeException.class)
                .hasMessageContaining("Short code cannot be null or empty");
        }
    }
} 