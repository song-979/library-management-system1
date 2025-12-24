package org.example.model;

public class Statistics {
    private int totalBooks;
    private int totalCopies;
    private int availableCopies;
    private int activeBorrowCount;
    private int returnedCount;
    private int readerCount;

    public Statistics(int totalBooks, int totalCopies, int availableCopies, int activeBorrowCount, int returnedCount, int readerCount) {
        this.totalBooks = totalBooks;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.activeBorrowCount = activeBorrowCount;
        this.returnedCount = returnedCount;
        this.readerCount = readerCount;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public int getActiveBorrowCount() { return activeBorrowCount; }
    public int getReturnedCount() { return returnedCount; }
    public int getReaderCount() { return readerCount; }
}
