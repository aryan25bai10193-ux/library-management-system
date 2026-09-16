package com.vit.library.dao;

import com.vit.library.exception.BookNotFoundException;
import com.vit.library.model.Book;
import com.vit.library.model.BookStatus;
import com.vit.library.model.Searchable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO implements Searchable<Book> {

    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, genre, publication_year, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getGenre());
            ps.setInt(5, book.getPublicationYear());
            ps.setString(6, book.getStatus().name());
            ps.executeUpdate();
        }
    }

    public Book getByIsbn(String isbn) throws SQLException, BookNotFoundException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new BookNotFoundException(isbn);
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    public void updateStatus(String isbn, BookStatus status) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE isbn = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, isbn);
            ps.executeUpdate();
        }
    }

    public boolean deleteBook(String isbn) throws SQLException {
        String sql = "DELETE FROM books WHERE isbn = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean exists(String isbn) throws SQLException {
        String sql = "SELECT 1 FROM books WHERE isbn = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Book> search(String keyword) {
        List<Book> results = new ArrayList<>();
        try {
            for (Book b : getAllBooks()) {
                if (b.matches(keyword)) {
                    results.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Search failed due to a database error: " + e.getMessage());
        }
        return results;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getInt("publication_year")
        );
        book.setStatus(BookStatus.valueOf(rs.getString("status")));
        return book;
    }
}
