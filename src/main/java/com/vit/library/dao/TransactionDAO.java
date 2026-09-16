package com.vit.library.dao;

import com.vit.library.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public int createTransaction(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (isbn, member_id, issue_date, due_date, return_date, renewed, fine_charged) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getIsbn());
            ps.setString(2, t.getMemberId());
            ps.setString(3, t.getIssueDate().toString());
            ps.setString(4, t.getDueDate().toString());
            ps.setString(5, t.getReturnDate() == null ? null : t.getReturnDate().toString());
            ps.setInt(6, 0);
            ps.setDouble(7, t.getFineCharged());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to obtain generated transaction ID");
    }

    public void markReturned(int transactionId, LocalDate returnDate, double fineCharged) throws SQLException {
        String sql = "UPDATE transactions SET return_date = ?, fine_charged = ? WHERE transaction_id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate.toString());
            ps.setDouble(2, fineCharged);
            ps.setInt(3, transactionId);
            ps.executeUpdate();
        }
    }

    public void updateDueDateAndRenewed(int transactionId, LocalDate newDueDate) throws SQLException {
        String sql = "UPDATE transactions SET due_date = ?, renewed = 1 WHERE transaction_id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newDueDate.toString());
            ps.setInt(2, transactionId);
            ps.executeUpdate();
        }
    }

    public List<Transaction> getOpenTransactionsForMember(String memberId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE member_id = ? AND return_date IS NULL";
        return queryList(sql, memberId);
    }

    public List<Transaction> getOpenTransactionForBook(String isbn) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE isbn = ? AND return_date IS NULL";
        return queryList(sql, isbn);
    }

    public List<Transaction> getAllOpenTransactions() throws SQLException {
        List<Transaction> results = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE return_date IS NULL ORDER BY due_date";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> results = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_id DESC";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    private List<Transaction> queryList(String sql, String param) throws SQLException {
        List<Transaction> results = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction(
                rs.getInt("transaction_id"),
                rs.getString("isbn"),
                rs.getString("member_id"),
                LocalDate.parse(rs.getString("issue_date")),
                LocalDate.parse(rs.getString("due_date"))
        );
        String returnDate = rs.getString("return_date");
        if (returnDate != null) {
            t.setReturnDate(LocalDate.parse(returnDate));
        }
        t.setFineCharged(rs.getDouble("fine_charged"));
        return t;
    }
}
