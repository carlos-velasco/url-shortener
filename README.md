# URL Shortener

A simple URL shortening service built with Micronaut.

## Features

- Shorten long URLs into compact, shareable links
- Redirect from short URLs to original destinations

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle
- Docker and Docker Compose (for containerized deployment)

### Running the Application

#### Using Gradle

```bash
./gradlew run
```

#### Using Docker

1. Create a `.env` file in the project root. Below is an example configuration with development values:

```env
# Database Configuration
# These are example values for development only
POSTGRES_DB=urlshortener
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Application Configuration
# These are example values for development only
DATASOURCES_DEFAULT_URL=jdbc:postgresql://db:5432/urlshortener
DATASOURCES_DEFAULT_USERNAME=postgres
DATASOURCES_DEFAULT_PASSWORD=postgres
```

2. Start the application:
```bash
docker compose up -d
```

3. The application will be available at `http://localhost:8080`

4. To stop the application:
```bash
docker compose down
```

## API Endpoints

### Create Short URL

Creates a short URL for a given long URL.

- **URL**: `/shorten`
- **Method**: `POST`
- **Content-Type**: `application/json`

#### Request Body
```json
{
    "url": "https://example.com/very/long/url/that/needs/shortening"
}
```

#### Response
- **Status Code**: `201 Created`
- **Content-Type**: `application/json`
- **Body**:
```json
{
    "shortUrl": "http://localhost:8080/abc123"
}
```

### Redirect to Original URL

Redirects to the original URL using the short code.

- **URL**: `/{shortCode}`
- **Method**: `GET`

#### Response
- **Status Code**: `301 Moved Permanently`
- **Location**: Original URL
- **Cache-Control**: `no-cache`

#### Error Responses
- **Status Code**: `404 Not Found`
  - When the short code doesn't exist
- **Status Code**: `400 Bad Request`
  - When the short code format is invalid
