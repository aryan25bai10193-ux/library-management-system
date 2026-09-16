package com.vit.library.exception;

public class InvalidISBNException extends RuntimeException {
    public InvalidISBNException(String isbn) {
        super("Invalid ISBN format: '" + isbn + "'. Expected 10 or 13 digits (hyphens allowed).");
    }
}
