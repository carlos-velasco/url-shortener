package com.velasconino.infrastructure.adapters.input.dto;

import io.micronaut.serde.annotation.Serdeable;
 
/**
 * Request DTO for URL shortening.
 */
@Serdeable
public record UrlRequestDto(String url) {} 