package com.vit.library.model;

import java.util.Objects;

public class Book {

    private final String isbn;
    private String title;
    private String author;
    private String genre;
    private final int publicationYear;
    private BookStatus status;

    public Book(String isbn, String title, String author, String genre, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.status = BookStatus.AVAILABLE;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) return false;
        String k = keyword.toLowerCase();
        return title.toLowerCase().contains(k)
                || author.toLowerCase().contains(k)
                || genre.toLowerCase().contains(k)
                || isbn.toLowerCase().contains(k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("%-13s | %-30s | %-20s | %-15s | %d | %s",
                isbn, title, author, genre, publicationYear, status.label());
    }
}
