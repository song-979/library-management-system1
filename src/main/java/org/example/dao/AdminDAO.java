package org.example.dao;

import org.example.model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // 验证管理员登录
    public Admin validateAdmin(String username, String password) {
        String sql = "SELECT username, password FROM admins WHERE username=? AND password=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Admin(rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("管理员验证失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}