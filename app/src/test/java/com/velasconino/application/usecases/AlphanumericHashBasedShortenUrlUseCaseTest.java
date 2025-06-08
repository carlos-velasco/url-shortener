package com.velasconino.application.usecases;

import com.velasconino.application.exceptions.InvalidUrlException;
import com.velasconino.application.exceptions.UrlShorteningCollisionException;
import com.velasconino.application.ports.input.ShortenUrlCommand;
import com.velasconino.application.ports.input.UrlShortenedResponse;
import com.velasconino.domain.HashBasedAlphanumericShortener;
import com.velasconino.infrastructure.adapters.output.InMemoryUrlRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphanumericHashBasedShortenUrlUseCaseTest {

    private static final int INITIAL_CODE_LENGTH = 8;
    private static final int MAX_CODE_LENGTH_INCREASE = 2;
    private static final String BASE_SHORT_URL = "https://myshortener.com/";
    
    private final InMemoryUrlRepository urlRepository = new InMemoryUrlRepository();
    private final AlphanumericHashBasedShortenUrlUseCase useCase = new AlphanumericHashBasedShortenUrlUseCase(
        urlRepository, INITIAL_CODE_LENGTH, MAX_CODE_LENGTH_INCREASE, BASE_SHORT_URL);

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
                secondLongUrl, List.of(initialShortCode));
        
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
    void shouldThrowUrlShorteningCollisionExceptionWhenMaxLengthIncreaseExceeded() {
        // Given - create an initial URL and get its short code
        String firstLongUrl = aUniqueUrl();
        UrlShortenedResponse firstUrlResponse = useCase.shortenUrl(new ShortenUrlCommand(firstLongUrl));
        String initialShortCode = firstUrlResponse.shortCode();
        
        // When - create a series of URLs that will force collisions at each length
        // We'll create URLs that collide at each length from initial to max length
        String currentShortCode = initialShortCode;
        List<String> shortCodes = new ArrayList<>();
        shortCodes.add(initialShortCode);
        
        // Create URLs that will collide at each length up to the maximum allowed increase
        for (int i = 0; i < MAX_CODE_LENGTH_INCREASE; i++) {
            currentShortCode = createShortUrlWithForcedCollision(aUniqueUrl(), shortCodes).shortCode();
            shortCodes.add(currentShortCode);
        }
        
        // Then - verify the last generated code is at the maximum allowed length
        assertThat(currentShortCode).hasSize(INITIAL_CODE_LENGTH + MAX_CODE_LENGTH_INCREASE);
        
        // When - try to create another URL that would require exceeding the maximum length
        String thirdLongUrl = aUniqueUrl();
        
        // Then - verify that attempting to create a URL that would exceed max length throws the expected exception
        assertThatThrownBy(() -> createShortUrlWithForcedCollision(thirdLongUrl, shortCodes))
            .isInstanceOf(UrlShorteningCollisionException.class)
            .hasMessageContaining("Could not generate a unique short code within the maximum allowed length increase");
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
     * Creates a short URL by forcing collisions with existing short codes.
     * This method simulates the scenario where the URL shortener needs to generate
     * increasingly longer codes due to collisions with existing codes.
     * 
     * The method works by:
     * 1. Creating a test implementation of the URL shortener
     * 2. For each existing short code, generating a collision by:
     *    - Creating a short code of the same length as the existing one
     *    - Saving it in the repository with a slightly modified URL
     * 3. Then delegating to the real implementation, which will detect these collisions
     *    and generate a longer code
     *
     * @param longUrl The URL to shorten
     * @param existingShortCodes List of short codes that should cause collisions
     * @return The response containing the new short code and URL
     */
    private UrlShortenedResponse createShortUrlWithForcedCollision(String longUrl, List<String> existingShortCodes) {
        // Create a test implementation that will generate collisions with existing codes
        AlphanumericHashBasedShortenUrlUseCase collisionTestUseCase = new AlphanumericHashBasedShortenUrlUseCase(
            urlRepository, INITIAL_CODE_LENGTH, MAX_CODE_LENGTH_INCREASE, BASE_SHORT_URL) {
            @Override
            public UrlShortenedResponse shortenUrl(ShortenUrlCommand command) {
                // Create a shortener for the URL
                HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener(command.url());
                
                // For each existing short code, create a collision by generating a code of the same length
                existingShortCodes.forEach(
                        existingShortCode -> {
                            String generatedShortCode = shortener.generateShortCode(existingShortCode.length());
                            // Append a random UUID to ensure the URL is unique and different from the original
                            var longUrlToSave = command.url() + UUID.randomUUID();
                            urlRepository.save(generatedShortCode, longUrlToSave);
                        }
                );
                // Then delegate to the real implementation, which will detect the collisions
                // and generate a longer code
                return super.shortenUrl(command);
            }
        };
        
        // This should detect the collisions and create a longer code
        return collisionTestUseCase.shortenUrl(new ShortenUrlCommand(longUrl));
    }
} 