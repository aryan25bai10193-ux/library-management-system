package com.vit.library.dao;

import com.vit.library.model.Librarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrarianDAO {

    public void addLibrarian(Librarian librarian) throws SQLException {
        String sql = "INSERT INTO librarians (id, name, email, phone, staff_id) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, librarian.getId());
            ps.setString(2, librarian.getName());
            ps.setString(3, librarian.getEmail());
            ps.setString(4, librarian.getPhone());
            ps.setString(5, librarian.getStaffId());
            ps.executeUpdate();
        }
    }

    public Optional<Librarian> getById(String id) throws SQLException {
        String sql = "SELECT * FROM librarians WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Librarian> getAllLibrarians() throws SQLException {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT * FROM librarians ORDER BY name";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean exists(String id) throws SQLException {
        String sql = "SELECT 1 FROM librarians WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Librarian mapRow(ResultSet rs) throws SQLException {
        return new Librarian(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("staff_id")
        );
    }
}
