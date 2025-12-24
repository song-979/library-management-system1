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
}
