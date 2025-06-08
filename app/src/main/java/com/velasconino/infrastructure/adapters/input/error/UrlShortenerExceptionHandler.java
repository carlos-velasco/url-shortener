package com.velasconino.infrastructure.adapters.input.error;

import com.velasconino.application.exceptions.EmptyUrlException;
import com.velasconino.application.exceptions.InvalidShortCodeException;
import com.velasconino.application.exceptions.InvalidUrlException;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;

/**
 * Exception handler for URL shortener specific exceptions.
 * Maps custom exceptions to appropriate HTTP responses.
 */
@Singleton
@Produces
@Requires(classes = {RuntimeException.class, ExceptionHandler.class})
public class UrlShortenerExceptionHandler implements ExceptionHandler<RuntimeException, HttpResponse<?>> {

    @Override
    public HttpResponse<?> handle(HttpRequest request, RuntimeException exception) {
        if (exception instanceof EmptyUrlException) {
            return HttpResponse.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.getCode(), exception.getMessage()));
        }
        
        if (exception instanceof InvalidUrlException) {
            return HttpResponse.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.getCode(), exception.getMessage()));
        }
        
        if (exception instanceof InvalidShortCodeException) {
            return HttpResponse.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.getCode(), exception.getMessage()));
        }
        
        // Default case for other runtime exceptions
        return HttpResponse.serverError()
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "An unexpected error occurred"));
    }
    
    /**
     * Simple error response DTO.
     */
    @Serdeable
    public record ErrorResponse(int status, String message) {}
} 