package com.velasconino.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashBasedAlphanumericShortenerTest {

    @Test
    void shouldCreateHashBasedAlphanumericShortener() {
        String url = "https://example.com/path";
        HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener(url);
        
        assertThat(shortener.getUrl()).isEqualTo(url);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 8, 10})
    void shouldGenerateShortCodeWithRequestedLength(int length) {
        HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener("https://example.com/path");
        
        String shortCode = shortener.generateShortCode(length);
        
        assertThat(shortCode).hasSize(length);
        assertThat(shortCode).matches("[A-Za-z0-9]+");
    }
    
    @Test
    void shouldThrowExceptionWhenLengthIsNegative() {
        HashBasedAlphanumericShortener shortener = new HashBasedAlphanumericShortener("https://example.com/path");
        
        assertThatThrownBy(() -> shortener.generateShortCode(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Length must be positive");
    }
    
    @Test
    void shouldGenerateDifferentCodesForDifferentUrls() {
        HashBasedAlphanumericShortener url1 = new HashBasedAlphanumericShortener("https://example.com/path1");
        HashBasedAlphanumericShortener url2 = new HashBasedAlphanumericShortener("https://example.com/path2");
        
        String code1 = url1.generateShortCode(8);
        String code2 = url2.generateShortCode(8);
        
        assertThat(code1).isNotEqualTo(code2);
    }
    
    @Test
    void shouldGenerateSameCodeForSameUrl() {
        HashBasedAlphanumericShortener url1 = new HashBasedAlphanumericShortener("https://example.com/path");
        HashBasedAlphanumericShortener url2 = new HashBasedAlphanumericShortener("https://example.com/path");
        
        String code1 = url1.generateShortCode(8);
        String code2 = url2.generateShortCode(8);
        
        assertThat(code1).isEqualTo(code2);
    }
} 