package com.velasconino.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@Testcontainers
class UrlShortenerE2ETest {

    @Container
    private static DockerComposeContainer<?> environment = new DockerComposeContainer<>(
            new File("../docker-compose.yml"))
            .withBuild(true)
            .withExposedService("app", 8080, Wait.forListeningPort())
            .withExposedService("db", 5432, Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(2));

    private String baseUrl;

    @BeforeEach
    void setUp() {
        String appHost = environment.getServiceHost("app", 8080);
        baseUrl = String.format("http://%s:%d", appHost, 8080);
    }

    @Test
    void shouldCreateAndResolveDifferentUrls() {
        // Given
        String url1 = aLongUrl();
        String url2 = aLongUrl();

        // When - Create two short URLs
        String shortUrl1 = createShortUrl(url1);
        String shortUrl2 = createShortUrl(url2);

        // Then - Extract short codes
        String shortCode1 = extractShortCode(shortUrl1);
        String shortCode2 = extractShortCode(shortUrl2);

        // Verify codes are different
        assertThat(shortCode1).isNotEqualTo(shortCode2);

        // When - Use both short codes
        String resolvedUrl1 = resolveUrlByShortCode(shortCode1);
        String resolvedUrl2 = resolveUrlByShortCode(shortCode2);

        // Then - Verify correct redirects
        assertThat(resolvedUrl1).isEqualTo(url1);
        assertThat(resolvedUrl2).isEqualTo(url2);
    }

    private String aLongUrl() {
        return "https://example.com/test-" + UUID.randomUUID();
    }

    private String createShortUrl(String originalUrl) {
        return given()
                .contentType(JSON)
                .body(Map.of("url", originalUrl))
                .when()
                .post(baseUrl + "/shorten")
                .then()
                .statusCode(201)
                .contentType(JSON)
                .body("shortUrl", not(emptyOrNullString()))
                .extract()
                .path("shortUrl");
    }

    private String extractShortCode(String shortUrl) {
        return shortUrl.substring(shortUrl.lastIndexOf('/') + 1);
    }

    private String resolveUrlByShortCode(String shortCode) {
        return given()
                .redirects().follow(false)
                .when()
                .get(baseUrl + "/" + shortCode)
                .then()
                .statusCode(301)
                .extract()
                .header("Location");
    }
} 