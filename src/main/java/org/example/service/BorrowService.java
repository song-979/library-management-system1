package org.example.service;

import org.example.model.BorrowRecord;
import java.util.List;

public interface BorrowService {
    String borrow(int readerId, int bookId, int qty);
    boolean returnBook(int readerId, int bookId, int qty);
    List<BorrowRecord> getActiveBorrows(int readerId);
    boolean returnByRecordId(int recordId);
    List<BorrowRecord> getAllHistory();
    boolean createHistory(int readerId, int bookId, int qty, String status, String borrowDate, String returnDate);
    boolean updateHistory(int id, int readerId, int bookId, int qty, String status, String borrowDate, String returnDate);
    boolean deleteHistory(int id);
}
