package com.velasconino.infrastructure.adapters.output;

import com.velasconino.infrastructure.adapters.output.persistence.UrlMappingEntity;
import com.velasconino.infrastructure.adapters.output.persistence.UrlMappingRepository;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static com.velasconino.fixture.ShortCodeFixture.aUniqueShortCode;

@MicronautTest
class PostgresUrlRepositoryTest {

    @Inject
    PostgresUrlRepository repository;
    
    @Inject
    UrlMappingRepository urlMappingRepository;
    
    @Test
    void shouldSaveAndRetrieveUrl() {
        // Given
        Instant testStartTime = Instant.now();
        String shortCode = aUniqueShortCode();
        String originalUrl = aUniqueUrl();
        
        // When
        repository.save(shortCode, originalUrl);
        Optional<String> result = repository.findOriginalUrlByShortCode(shortCode);
        
        Instant testEndTime = Instant.now();
        
        // Then
        assertThat(result).isPresent().contains(originalUrl);
        
        // Verify entity was saved correctly
        Optional<UrlMappingEntity> entity = urlMappingRepository.findByShortCode(shortCode);
        assertThat(entity).isPresent();
        assertThat(entity.get().getOriginalUrl()).isEqualTo(originalUrl);
        assertThat(entity.get().getShortCode()).isEqualTo(shortCode);
        
        // Verify timestamp is between test start and end time
        Instant createdAt = entity.get().getCreatedAt();
        assertThat(createdAt)
            .isNotNull()
            .isBetween(testStartTime, testEndTime);
    }
    
    @Test
    void shouldReturnEmptyWhenShortCodeNotFound() {
        // Given
        String nonExistentShortCode = aUniqueShortCode();
        
        // When
        Optional<String> result = repository.findOriginalUrlByShortCode(nonExistentShortCode);
        
        // Then
        assertThat(result).isEmpty();
    }
} 