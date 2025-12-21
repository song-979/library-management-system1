package org.example.service;

import org.example.model.BorrowRecord;
import java.util.List;

public interface BorrowService {
    boolean borrow(int readerId, int bookId, int qty);
    boolean returnBook(int readerId, int bookId, int qty);
    List<BorrowRecord> getActiveBorrows(int readerId);
    boolean returnByRecordId(int recordId);
}
