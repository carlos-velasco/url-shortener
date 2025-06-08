package com.velasconino.infrastructure.adapters.input.dto;

import io.micronaut.serde.annotation.Serdeable;
 
/**
 * Response DTO for URL shortening.
 */
@Serdeable
public record UrlResponseDto(String shortUrl) {} 