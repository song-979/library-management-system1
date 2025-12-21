package org.example.dao;

import org.example.model.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAO {

    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT id, name, code, phone, max_borrow, status, created_time, updated_time FROM readers";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                readers.add(new Reader(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("code"),
                        rs.getString("phone"),
                        rs.getInt("max_borrow"),
                        rs.getString("status"),
                        rs.getString("created_time"),
                        rs.getString("updated_time")
                ));
            }
        } catch (SQLException e) {
            System.err.println("查询读者失败：" + e.getMessage());
            e.printStackTrace();
        }
        return readers;
    }

    public boolean addReader(Reader reader) {
        String sql = "INSERT INTO readers (name, code, phone, max_borrow, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reader.getName());
            pstmt.setString(2, reader.getCode());
            pstmt.setString(3, reader.getPhone());
            pstmt.setInt(4, reader.getMaxBorrow());
            pstmt.setString(5, reader.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("新增读者失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReader(Reader reader) {
        String sql = "UPDATE readers SET name=?, code=?, phone=?, max_borrow=?, status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reader.getName());
            pstmt.setString(2, reader.getCode());
            pstmt.setString(3, reader.getPhone());
            pstmt.setInt(4, reader.getMaxBorrow());
            pstmt.setString(5, reader.getStatus());
            pstmt.setInt(6, reader.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新读者失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReader(int readerId) {
        String sql = "DELETE FROM readers WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, readerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除读者失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
