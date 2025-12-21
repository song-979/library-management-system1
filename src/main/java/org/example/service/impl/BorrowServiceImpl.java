package org.example.service.impl;

import org.example.dao.BorrowDAO;
import org.example.dao.BookDAO;
import org.example.dao.DBConnection;
import org.example.service.BorrowService;
import org.example.model.BorrowRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BorrowServiceImpl implements BorrowService {
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    public boolean borrow(int readerId, int bookId, int qty) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            boolean ok = bookDAO.decreaseForBorrow(conn, bookId, qty);
            if (!ok) { conn.rollback(); return false; }
            ok = borrowDAO.insertBorrow(conn, readerId, bookId, qty);
            if (!ok) { conn.rollback(); return false; }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnBook(int readerId, int bookId, int qty) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            boolean ok = bookDAO.increaseForReturn(conn, bookId, qty);
            if (!ok) { conn.rollback(); return false; }
            ok = borrowDAO.insertBorrow(conn, readerId, bookId, -qty);
            if (!ok) { conn.rollback(); return false; } // 记录负数量作为操作日志
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BorrowRecord> getActiveBorrows(int readerId) {
        return borrowDAO.getActiveBorrowsByReader(readerId);
    }

    @Override
    public boolean returnByRecordId(int recordId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            BorrowRecord rec = borrowDAO.fetchRecordForUpdate(conn, recordId);
            if (rec == null || !"borrowed".equals(rec.getStatus())) { conn.rollback(); return false; }
            boolean ok = bookDAO.increaseForReturn(conn, rec.getBookId(), rec.getQuantity());
            if (!ok) { conn.rollback(); return false; }
            ok = borrowDAO.markReturned(conn, recordId);
            if (!ok) { conn.rollback(); return false; }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
