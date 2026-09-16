package com.vit.library.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = DB_FOLDER + File.separator + "library.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    private static Connection connection;

    private DatabaseManager() {

    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            new File(DB_FOLDER).mkdirs();
            connection = DriverManager.getConnection(URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");
        }
        return connection;
    }

    public static void initializeSchema() throws SQLException {
        String books = """
            CREATE TABLE IF NOT EXISTS books (
                isbn TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                genre TEXT,
                publication_year INTEGER,
                status TEXT NOT NULL DEFAULT 'AVAILABLE'
            );
            """;

        String members = """
            CREATE TABLE IF NOT EXISTS members (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                membership_type TEXT NOT NULL,
                join_date TEXT NOT NULL,
                outstanding_fine REAL NOT NULL DEFAULT 0.0
            );
            """;

        String librarians = """
            CREATE TABLE IF NOT EXISTS librarians (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                staff_id TEXT NOT NULL
            );
            """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                isbn TEXT NOT NULL,
                member_id TEXT NOT NULL,
                issue_date TEXT NOT NULL,
                due_date TEXT NOT NULL,
                return_date TEXT,
                renewed INTEGER NOT NULL DEFAULT 0,
                fine_charged REAL NOT NULL DEFAULT 0.0,
                FOREIGN KEY (isbn) REFERENCES books(isbn),
                FOREIGN KEY (member_id) REFERENCES members(id)
            );
            """;

        try (Statement st = getConnection().createStatement()) {
            st.execute(books);
            st.execute(members);
            st.execute(librarians);
            st.execute(transactions);
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Warning: failed to close database connection cleanly: " + e.getMessage());
        }
    }
}
