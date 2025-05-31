package com.velasconino.api;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import com.velasconino.api.dto.UrlRequestDto;
import com.velasconino.api.dto.UrlResponseDto;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import org.junit.jupiter.api.BeforeEach;

@MicronautTest
class UrlShortenerControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    DefaultHttpClientConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration.setFollowRedirects(false);
    }

    @Test
    void testCreateShortUrl() {
        var originalUrl = "https://example.com/very/long/url";
        var request = new UrlRequestDto(originalUrl);

        var response = client.toBlocking()
                .exchange(HttpRequest.POST("/shorten", request), UrlResponseDto.class);

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body().shortUrl()).startsWith("https://myshortener.com/");
    }

    @Test
    void testRedirectToOriginal() {
        var response = client.toBlocking()
                .exchange(HttpRequest.GET("/abc123654"));

        assertThat(response.status().getCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.getCode());
        assertThat((String)response.getHeaders().get("Location")).startsWith("https://example.com/");
    }
} 