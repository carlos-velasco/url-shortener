package com.velasconino.application.ports.input;

import com.velasconino.application.exceptions.EmptyUrlException;
import com.velasconino.application.exceptions.InvalidUrlException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.velasconino.fixture.UrlFixture.aUniqueUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShortenUrlCommandTest {

    @Test
    void shouldCreateCommandWithValidUrl() {
        // Given
        String validUrl = aUniqueUrl();
        
        // When
        ShortenUrlCommand command = new ShortenUrlCommand(validUrl);
        
        // Then
        assertThat(command.url()).isEqualTo(validUrl);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldThrowEmptyUrlExceptionWhenUrlIsInvalid(String invalidUrl) {
        assertThatThrownBy(() -> new ShortenUrlCommand(invalidUrl))
            .isInstanceOf(EmptyUrlException.class)
            .hasMessageContaining("URL cannot be null or empty");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "not-a-valid-url",
        "http://",
        "https://",
        "ftp://",
        "example.com",
        "www.example.com"
    })
    void shouldThrowInvalidUrlExceptionWhenUrlIsInvalid(String invalidUrl) {
        assertThatThrownBy(() -> new ShortenUrlCommand(invalidUrl))
            .isInstanceOf(InvalidUrlException.class)
            .hasMessageContaining("Invalid URL format");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "https://example.com",
        "http://example.com/path",
        "https://subdomain.example.com/path?query=value",
        "ftp://ftp.example.com/files",
        "https://example.com/path/to/resource#fragment"
    })
    void shouldAcceptValidUrls(String validUrl) {
        // When
        ShortenUrlCommand command = new ShortenUrlCommand(validUrl);
        
        // Then
        assertThat(command.url()).isEqualTo(validUrl);
    }
} 