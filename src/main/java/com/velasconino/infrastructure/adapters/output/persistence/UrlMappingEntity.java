package com.velasconino.infrastructure.adapters.output.persistence;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

import java.time.Instant;

/**
 * Entity representing a URL mapping in the database.
 */
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
    
    // Default constructor required by Micronaut Data
    public UrlMappingEntity() {
    }
    
    public UrlMappingEntity(String shortCode, String originalUrl) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
    }
    
    // Getters and setters
    
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public String getOriginalUrl() {
        return originalUrl;
    }
    
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
} 