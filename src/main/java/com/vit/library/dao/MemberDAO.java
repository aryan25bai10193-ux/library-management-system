package com.vit.library.dao;

import com.vit.library.exception.MemberNotFoundException;
import com.vit.library.model.Member;
import com.vit.library.model.MembershipType;
import com.vit.library.model.Searchable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO implements Searchable<Member> {

    public void addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (id, name, email, phone, membership_type, join_date, outstanding_fine) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getMembershipType().name());
            ps.setString(6, member.getJoinDate().toString());
            ps.setDouble(7, member.getOutstandingFine());
            ps.executeUpdate();
        }
    }

    public Member getById(String id) throws SQLException, MemberNotFoundException {
        String sql = "SELECT * FROM members WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        throw new MemberNotFoundException(id);
    }

    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                members.add(mapRow(rs));
            }
        }
        return members;
    }

    public void updateFine(String id, double newFine) throws SQLException {
        String sql = "UPDATE members SET outstanding_fine = ? WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newFine);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public boolean deleteMember(String id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean exists(String id) throws SQLException {
        String sql = "SELECT 1 FROM members WHERE id = ?";
        Connection conn = DatabaseManager.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Member> search(String keyword) {
        List<Member> results = new ArrayList<>();
        try {
            String k = keyword.toLowerCase();
            for (Member m : getAllMembers()) {
                if (m.getName().toLowerCase().contains(k)
                        || m.getId().toLowerCase().contains(k)
                        || (m.getEmail() != null && m.getEmail().toLowerCase().contains(k))) {
                    results.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Search failed due to a database error: " + e.getMessage());
        }
        return results;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                MembershipType.valueOf(rs.getString("membership_type")),
                LocalDate.parse(rs.getString("join_date"))
        );
        member.addFine(rs.getDouble("outstanding_fine"));
        return member;
    }
}
