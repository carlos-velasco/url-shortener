package com.velasconino.infrastructure.config;

import com.velasconino.application.ports.output.UrlRepository;
import com.velasconino.infrastructure.adapters.output.PostgresUrlRepository;
import com.velasconino.infrastructure.adapters.output.persistence.UrlMappingRepository;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * Factory for creating repository implementations based on the environment.
 */
@Factory
public class RepositoryFactory {
    
    /**
     * Creates a PostgreSQL implementation of the UrlRepository.
     * This is the primary implementation used in production.
     * 
     * @param urlMappingRepository The Micronaut Data repository for URL mappings
     * @return A PostgreSQL-backed implementation of UrlRepository
     */
    @Singleton
    public UrlRepository postgresUrlRepository(UrlMappingRepository urlMappingRepository) {
        return new PostgresUrlRepository(urlMappingRepository);
    }
} 