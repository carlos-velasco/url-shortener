package com.velasconino.application.usecases;

import com.velasconino.application.exceptions.InvalidUrlException;
import com.velasconino.application.ports.input.ShortenUrlCommand;
import com.velasconino.application.ports.input.UrlShortenedResponse;
import com.velasconino.domain.HashBasedAlphanumericShortener;
import com.velasconino.infrastructure.adapters.output.InMemoryUrlRepository;
import org.junit.jupiter.api.Test;

import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphanumericHashBasedShortenUrlUseCaseTest {

    private static final int INITIAL_CODE_LENGTH = 8;
    private static final String BASE_SHORT_URL = "https://myshortener.com/";
    
    private final InMemoryUrlRepository urlRepository = new InMemoryUrlRepository();
    private final AlphanumericHashBasedShortenUrlUseCase useCase = new AlphanumericHashBasedShortenUrlUseCase(urlRepository, INITIAL_CODE_LENGTH, BASE_SHORT_URL);

    @Test
    void shouldShortenUrlAndReturnResponse() {
        // Given
        String originalUrl = aUniqueUrl();
        ShortenUrlCommand command = new ShortenUrlCommand(originalUrl);

        // When
        UrlShortenedResponse response = useCase.shortenUrl(command);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.shortCode()).isNotEmpty();
        assertThat(response.shortCode()).hasSize(INITIAL_CODE_LENGTH);
        assertThat(response.shortUrl()).startsWith(BASE_SHORT_URL);
        
        // Verify the mapping was saved correctly
        assertThat(urlRepository.findOriginalUrlByShortCode(response.shortCode()))
            .isPresent()
            .contains(originalUrl);
    }

    @Test
    void shouldGenerateUniqueShortCodesForDifferentUrls() {
        // Given
        String url1 = aUniqueUrl();
        String url2 = aUniqueUrl();
        
        // When
        UrlShortenedResponse response1 = useCase.shortenUrl(new ShortenUrlCommand(url1));
        UrlShortenedResponse response2 = useCase.shortenUrl(new ShortenUrlCommand(url2));
        
        // Then
        assertThat(response1.shortCode()).isNotEqualTo(response2.shortCode());
        
        // Verify both mappings were saved correctly
        assertThat(urlRepository.findOriginalUrlByShortCode(response1.shortCode()))
            .isPresent()
            .contains(url1);
            
        assertThat(urlRepository.findOriginalUrlByShortCode(response2.shortCode()))
            .isPresent()
            .contains(url2);
    }

    @Test
    void shouldReuseSameShortCodeWhenShorteningTheSameUrlTwice() {
        // Given
        String url = aUniqueUrl();
        ShortenUrlCommand command = new ShortenUrlCommand(url);
        
        // When
        UrlShortenedResponse firstResponse = useCase.shortenUrl(command);
        UrlShortenedResponse secondResponse = useCase.shortenUrl(command);
        
        // Then
        assertThat(secondResponse.shortCode()).isEqualTo(firstResponse.shortCode());
        assertThat(secondResponse.shortUrl()).isEqualTo(firstResponse.shortUrl());
        
        // Verify the mapping exists only once in the repository
        assertThat(urlRepository.findOriginalUrlByShortCode(firstResponse.shortCode()))
            .isPresent()
            .contains(url);
    }

    @Test
    void shouldIncreaseLengthByOneWhenShortCodeCollisionOccurs() {
        // Given
        String firstLongUrl = aUniqueUrl();
        String secondLongUrl = aUniqueUrl();
        
        // First, create a short URL for the first long URL
        UrlShortenedResponse firstUrlResponse = useCase.shortenUrl(new ShortenUrlCommand(firstLongUrl));
        String initialShortCode = firstUrlResponse.shortCode();
        int initialCodeLength = initialShortCode.length();
        
        // When - create a short URL for the second long URL with a collision on the first attempt
        UrlShortenedResponse secondUrlResponse = createShortUrlWithForcedCollision(
                secondLongUrl, initialShortCode);
        
        // Then - verify the second short code is one character longer
        assertThat(secondUrlResponse.shortCode()).isNotEqualTo(initialShortCode);
        assertThat(secondUrlResponse.shortCode()).hasSize(initialCodeLength + 1);
        
        // Verify both mappings are intact
        assertThat(urlRepository.findOriginalUrlByShortCode(initialShortCode))
            .isPresent()
            .contains(firstLongUrl);
            
        assertThat(urlRepository.findOriginalUrlByShortCode(secondUrlResponse.shortCode()))
            .isPresent()
            .contains(secondLongUrl);
    }

    @Test
    void shouldThrowInvalidUrlExceptionWhenCommandHasInvalidUrl() {
        // Given an invalid URL
        String invalidUrl = "not-a-valid-url";
        
        // Then
        assertThatThrownBy(() -> new ShortenUrlCommand(invalidUrl))
            .isInstanceOf(InvalidUrlException.class)
            .hasMessageContaining("Invalid URL format");
    }

    /**
     * Creates a short URL with a forced collision on the first attempt.
     * This simulates the scenario where the first generated short code already exists,
     * forcing the use case to generate a longer code.
     *
     * @param longUrl The URL to shorten
     * @param existingShortCode The short code that will cause a collision
     * @return The response containing the new short code and URL
     */
    private UrlShortenedResponse createShortUrlWithForcedCollision(String longUrl, String existingShortCode) {
        // Create a test implementation that will generate the same short code initially
        AlphanumericHashBasedShortenUrlUseCase collisionTestUseCase = new AlphanumericHashBasedShortenUrlUseCase(urlRepository, INITIAL_CODE_LENGTH, BASE_SHORT_URL) {
            @Override
            public UrlShortenedResponse shortenUrl(ShortenUrlCommand command) {
                // Force a collision generating the short code and saving it a short code that already exists
                HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener(command.url());
                String shortCode = shortener.generateShortCode(existingShortCode.length());
                // Add a parameter to the long URL to avoid collisions with the existing URL
                var longUrlToSave = command.url() + "?param1=value1";
                urlRepository.save(shortCode, longUrlToSave);
                // Then delegate to the real implementation
                return super.shortenUrl(command);
            }
        };
        
        // This should detect the collision and create a longer code
        return collisionTestUseCase.shortenUrl(new ShortenUrlCommand(longUrl));
    }
} 