package org.example.dao;

import org.example.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // 查询所有图书
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, category, total_copies, available_copies, remarks, created_time, updated_time FROM books";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getString("remarks"),
                        rs.getString("created_time"),
                        rs.getString("updated_time")
                ));
            }
        } catch (SQLException e) {
            System.err.println("查询图书失败：" + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    // 新增图书
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, category, total_copies, available_copies, remarks) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getCategory());
            pstmt.setInt(3, book.getTotalCopies());
            pstmt.setInt(4, book.getAvailableCopies());
            pstmt.setString(5, book.getRemarks());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("新增图书失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 更新图书
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, category=?, total_copies=?, available_copies=?, remarks=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getCategory());
            pstmt.setInt(3, book.getTotalCopies());
            pstmt.setInt(4, book.getAvailableCopies());
            pstmt.setString(5, book.getRemarks());
            pstmt.setInt(6, book.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新图书失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 删除图书
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除图书失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean decreaseForBorrow(Connection conn, int bookId, int qty) {
        String sql = "UPDATE books SET available_copies=available_copies-? WHERE id=? AND available_copies>=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, qty);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("借阅扣减失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean increaseForReturn(Connection conn, int bookId, int qty) {
        String sql = "UPDATE books SET available_copies=available_copies+? WHERE id=? AND available_copies+?<=total_copies";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, qty);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("归还增加失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> searchBooks(String keyword, String category, Integer minAvailable) {
        List<Book> books = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT id, title, category, total_copies, available_copies, remarks, created_time, updated_time FROM books WHERE 1=1");
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) { sb.append(" AND title LIKE ?"); params.add("%" + keyword + "%"); }
        if (category != null && !category.isEmpty()) { sb.append(" AND category = ?"); params.add(category); }
        if (minAvailable != null) { sb.append(" AND available_copies >= ?"); params.add(minAvailable); }
        sb.append(" ORDER BY updated_time DESC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("id"), rs.getString("title"), rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"), rs.getString("remarks"), rs.getString("created_time"), rs.getString("updated_time")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("搜索图书失败：" + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public List<String> getCategories() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM books ORDER BY category";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(rs.getString(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
