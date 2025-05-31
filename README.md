# URL Shortener

A simple URL shortening service built with Micronaut.

## Features

- Shorten long URLs into compact, shareable links
- Redirect from short URLs to original destinations

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle

### Running the Application

```bash
./gradlew run
```

## API Endpoints

- `POST /shorten` - Create a short URL
- `GET /{shortCode}` - Redirect to the original URL
