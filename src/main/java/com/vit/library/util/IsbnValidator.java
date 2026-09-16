package com.vit.library.util;

import com.vit.library.exception.InvalidISBNException;

public final class IsbnValidator {

    private IsbnValidator() {
    }

    public static String validateAndNormalize(String rawIsbn) {
        if (rawIsbn == null) {
            throw new InvalidISBNException("null");
        }
        String cleaned = rawIsbn.replace("-", "").trim();
        if (!cleaned.matches("\\d{10}|\\d{13}")) {
            throw new InvalidISBNException(rawIsbn);
        }
        return cleaned;
    }
}
