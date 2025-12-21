package org.example.model;

public class BorrowRecord {
    private int id;
    private int readerId;
    private int bookId;
    private int quantity;
    private String status;
    private String borrowDate;
    private String returnDate;
    private String bookTitle;

    public BorrowRecord(int id, int readerId, int bookId, int quantity, String status, String borrowDate, String returnDate, String bookTitle) {
        this.id = id;
        this.readerId = readerId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.status = status;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.bookTitle = bookTitle;
    }

    public int getId() { return id; }
    public int getReaderId() { return readerId; }
    public int getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getBorrowDate() { return borrowDate; }
    public String getReturnDate() { return returnDate; }
    public String getBookTitle() { return bookTitle; }
}
