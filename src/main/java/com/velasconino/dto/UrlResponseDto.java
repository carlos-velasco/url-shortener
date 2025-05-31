package com.velasconino.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UrlResponseDto(String shortUrl) {} 