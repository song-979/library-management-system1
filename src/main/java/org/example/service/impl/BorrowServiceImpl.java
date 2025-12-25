package org.example.service.impl;

import org.example.dao.BorrowDAO;
import org.example.dao.BookDAO;
import org.example.dao.DBConnection;
import org.example.dao.ReaderDAO;
import org.example.service.BorrowService;
import org.example.model.BorrowRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BorrowServiceImpl implements BorrowService {
    private final BookDAO bookDAO = new BookDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final ReaderDAO readerDAO = new ReaderDAO();

    @Override
    public String borrow(int readerId, int bookId, int qty) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int current = readerDAO.sumActiveBorrowQty(readerId);
            int max = 5;
            try { max = readerDAO.getAllReaders().stream().filter(r -> r.getId() == readerId).findFirst().map(r -> r.getMaxBorrow()).orElse(5); } catch (Exception ignore) {}
            if (current + qty > max) { 
                conn.rollback(); 
                return "超过最大借阅数量 (当前已借: " + current + ", 本次借: " + qty + ", 限额: " + max + ")"; 
            }
            boolean ok = bookDAO.decreaseForBorrow(conn, bookId, qty);
            if (!ok) { 
                conn.rollback(); 
                return "库存不足或图书不存在"; 
            }
            ok = borrowDAO.insertBorrow(conn, readerId, bookId, qty);
            if (!ok) { 
                conn.rollback(); 
                return "系统错误：无法创建借阅记录"; 
            }
            conn.commit();
            return null; // success
        } catch (SQLException e) {
            e.printStackTrace();
            return "数据库异常: " + e.getMessage();
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

    @Override
    public List<BorrowRecord> getAllHistory() {
        return borrowDAO.getAllRecords();
    }

    @Override
    public boolean createHistory(int readerId, int bookId, int qty, String status, String borrowDate, String returnDate) {
        return borrowDAO.insertRecordManual(readerId, bookId, qty, status, borrowDate, returnDate);
    }

    @Override
    public boolean updateHistory(int id, int readerId, int bookId, int qty, String status, String borrowDate, String returnDate) {
        return borrowDAO.updateRecordManual(id, readerId, bookId, qty, status, borrowDate, returnDate);
    }

    @Override
    public boolean deleteHistory(int id) {
        return borrowDAO.deleteRecord(id);
    }
}
