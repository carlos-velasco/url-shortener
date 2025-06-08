package com.velasconino.infrastructure.adapters.output.persistence;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for CRUD operations on URL mappings.
 */
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UrlMappingRepository extends CrudRepository<UrlMappingEntity, String> {
    
    /**
     * Find the original URL by short code.
     * 
     * @param shortCode The short code to look up
     * @return An Optional containing the entity if found, or empty if not found
     */
    Optional<UrlMappingEntity> findByShortCode(String shortCode);
} 