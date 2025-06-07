package com.velasconino.infrastructure.adapters.output;

import com.velasconino.application.ports.output.UrlRepository;
import com.velasconino.infrastructure.adapters.output.persistence.UrlMappingEntity;
import com.velasconino.infrastructure.adapters.output.persistence.UrlMappingRepository;

import jakarta.inject.Singleton;
import java.util.Optional;

/**
 * PostgreSQL implementation of the UrlRepository.
 */
@Singleton
public class PostgresUrlRepository implements UrlRepository {
    
    private final UrlMappingRepository repository;
    
    public PostgresUrlRepository(UrlMappingRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void save(String shortCode, String originalUrl) {
        UrlMappingEntity entity = new UrlMappingEntity(shortCode, originalUrl);
        repository.save(entity);
    }
    
    @Override
    public Optional<String> findOriginalUrlByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlMappingEntity::getOriginalUrl);
    }
} 