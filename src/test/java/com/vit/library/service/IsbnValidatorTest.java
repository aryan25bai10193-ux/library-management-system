package com.vit.library.service;

import com.vit.library.exception.InvalidISBNException;
import com.vit.library.util.IsbnValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IsbnValidatorTest {

    @Test
    void acceptsValid13DigitIsbnWithHyphens() {
        assertEquals("9780132350884", IsbnValidator.validateAndNormalize("978-0-13-235088-4"));
    }

    @Test
    void acceptsValid10DigitIsbn() {
        assertEquals("0132350882", IsbnValidator.validateAndNormalize("0132350882"));
    }

    @Test
    void rejectsWrongLength() {
        assertThrows(InvalidISBNException.class, () -> IsbnValidator.validateAndNormalize("12345"));
    }

    @Test
    void rejectsNonNumericIsbn() {
        assertThrows(InvalidISBNException.class, () -> IsbnValidator.validateAndNormalize("ABCDEFGHIJ"));
    }
}
