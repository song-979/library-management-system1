package org.example.dao;

import org.example.model.BorrowRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    public boolean insertBorrow(Connection conn, int readerId, int bookId, int qty) {
        String sql = "INSERT INTO borrow_records (reader_id, book_id, quantity, status, borrow_date) VALUES (?, ?, ?, 'borrowed', NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, qty);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("写入借阅记录失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<BorrowRecord> getActiveBorrowsByReader(int readerId) {
        String sql = "SELECT br.id, br.reader_id, br.book_id, br.quantity, br.status, br.borrow_date, br.return_date, b.title " +
                "FROM borrow_records br JOIN books b ON br.book_id=b.id " +
                "WHERE br.reader_id=? AND br.status='borrowed' ORDER BY br.borrow_date DESC";
        List<BorrowRecord> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, readerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BorrowRecord(
                            rs.getInt("id"),
                            rs.getInt("reader_id"),
                            rs.getInt("book_id"),
                            rs.getInt("quantity"),
                            rs.getString("status"),
                            rs.getString("borrow_date"),
                            rs.getString("return_date"),
                            rs.getString("title")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("查询已借阅失败：" + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public BorrowRecord fetchRecordForUpdate(Connection conn, int recordId) throws SQLException {
        String sql = "SELECT br.id, br.reader_id, br.book_id, br.quantity, br.status, br.borrow_date, br.return_date, b.title " +
                "FROM borrow_records br JOIN books b ON br.book_id=b.id WHERE br.id=? FOR UPDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BorrowRecord(
                            rs.getInt("id"),
                            rs.getInt("reader_id"),
                            rs.getInt("book_id"),
                            rs.getInt("quantity"),
                            rs.getString("status"),
                            rs.getString("borrow_date"),
                            rs.getString("return_date"),
                            rs.getString("title")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public boolean markReturned(Connection conn, int recordId) {
        String sql = "UPDATE borrow_records SET status='returned', return_date=NOW() WHERE id=? AND status='borrowed'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新归还状态失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
