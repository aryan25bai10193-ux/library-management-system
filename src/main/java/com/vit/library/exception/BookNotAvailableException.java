package com.vit.library.exception;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String isbn) {
        super("Book with ISBN '" + isbn + "' is not available for issue right now.");
    }
}
