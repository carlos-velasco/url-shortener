package com.velasconino.infrastructure.adapters.output.persistence;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entity representing a URL mapping in the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedEntity("url_mapping")
public class UrlMappingEntity {
    
    @Id
    @MappedProperty("short_code")
    private String shortCode;
    
    @MappedProperty("original_url")
    private String originalUrl;
    
    @DateCreated
    @MappedProperty("created_at")
    private Instant createdAt;
    
    public UrlMappingEntity(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }
} 