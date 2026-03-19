package uk.gov.hmcts.reform.dev.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FutureDateTimeValidatorTest {

    private FutureDateTimeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FutureDateTimeValidator();
    }

    @Test
    void shouldReturnTrue_whenDateIsInFuture() {
        assertTrue(validator.isValid(LocalDateTime.now().plusDays(1), null));
    }

    @Test
    void shouldReturnFalse_whenDateIsInPast() {
        assertFalse(validator.isValid(LocalDateTime.now().minusDays(1), null));
    }

    @Test
    void shouldReturnTrue_whenDateIsNull() {
        assertTrue(validator.isValid(null, null));
    }
}
