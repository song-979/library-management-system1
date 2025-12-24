package org.example.dao;

import org.example.model.Statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDAO {
    public Statistics fetch() {
        int totalBooks = 0;
        int totalCopies = 0;
        int availableCopies = 0;
        int activeBorrow = 0;
        int returned = 0;
        int readerCount = 0;
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) cnt, COALESCE(SUM(total_copies),0) tc, COALESCE(SUM(available_copies),0) ac FROM books")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalBooks = rs.getInt("cnt");
                        totalCopies = rs.getInt("tc");
                        availableCopies = rs.getInt("ac");
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) c FROM borrow_records WHERE status='borrowed'")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) activeBorrow = rs.getInt("c"); }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) c FROM borrow_records WHERE status='returned'")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) returned = rs.getInt("c"); }
            }
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) c FROM readers")) {
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) readerCount = rs.getInt("c"); }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Statistics(totalBooks, totalCopies, availableCopies, activeBorrow, returned, readerCount);
    }

    public java.util.List<org.example.model.Book> topBorrowedBooks(int limit) {
        String sql = "SELECT b.id, b.title, b.category, b.total_copies, b.available_copies, b.remarks, b.created_time, b.updated_time, COUNT(br.id) cnt " +
                "FROM borrow_records br JOIN books b ON br.book_id=b.id GROUP BY b.id ORDER BY cnt DESC LIMIT ?";
        java.util.List<org.example.model.Book> list = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new org.example.model.Book(
                            rs.getInt("id"), rs.getString("title"), rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"), rs.getString("remarks"), rs.getString("created_time"), rs.getString("updated_time")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public java.util.List<org.example.model.CategoryReport> categoryReport() {
        String sql = "SELECT b.category AS cat, COUNT(DISTINCT b.id) AS titles, COALESCE(SUM(b.total_copies),0) AS total, COALESCE(SUM(b.available_copies),0) AS avail, " +
                "COALESCE(SUM(CASE WHEN br.status='borrowed' THEN 1 ELSE 0 END),0) AS borrows " +
                "FROM books b LEFT JOIN borrow_records br ON br.book_id=b.id GROUP BY b.category ORDER BY cat";
        java.util.List<org.example.model.CategoryReport> list = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new org.example.model.CategoryReport(rs.getString("cat"), rs.getInt("titles"), rs.getInt("total"), rs.getInt("avail"), rs.getInt("borrows")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public java.util.List<org.example.model.BorrowRecord> overdueRecords(int days) {
        String sql = "SELECT br.id, br.reader_id, br.book_id, br.quantity, br.status, br.borrow_date, br.return_date, b.title " +
                "FROM borrow_records br JOIN books b ON br.book_id=b.id WHERE br.status='borrowed' AND br.borrow_date < DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY br.borrow_date ASC";
        java.util.List<org.example.model.BorrowRecord> list = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new org.example.model.BorrowRecord(rs.getInt("id"), rs.getInt("reader_id"), rs.getInt("book_id"), rs.getInt("quantity"), rs.getString("status"), rs.getString("borrow_date"), rs.getString("return_date"), rs.getString("title")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
