package com.velasconino.api.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UrlResponseDto(String shortUrl) {} 