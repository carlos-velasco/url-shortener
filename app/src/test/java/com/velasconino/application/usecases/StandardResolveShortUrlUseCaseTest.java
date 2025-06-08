package com.velasconino.application.usecases;

import com.velasconino.application.ports.input.ShortCodeQuery;
import com.velasconino.infrastructure.adapters.output.InMemoryUrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static com.velasconino.fixture.ShortCodeFixture.aUniqueShortCode;
import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static org.assertj.core.api.Assertions.assertThat;

class StandardResolveShortUrlUseCaseTest {

    private final InMemoryUrlRepository urlRepository = new InMemoryUrlRepository();
    private final StandardResolveShortUrlUseCase useCase = new StandardResolveShortUrlUseCase(urlRepository);

    @Test
    @DisplayName("Should return original URL when short code exists")
    void shouldReturnOriginalUrlWhenShortCodeExists() {
        // Given
        String shortCode = aUniqueShortCode();
        String originalUrl = aUniqueUrl();
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