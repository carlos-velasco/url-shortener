package com.velasconino.application.ports.input;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.velasconino.application.exceptions.InvalidShortCodeException;

class ShortCodeQueryTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldThrowExceptionWhenShortCodeIsInvalid(String shortCode) {
        // Then
        assertThatThrownBy(() -> new ShortCodeQuery(shortCode))
            .isInstanceOf(InvalidShortCodeException.class)
            .hasMessageContaining("Short code cannot be null or empty");
    }
}
