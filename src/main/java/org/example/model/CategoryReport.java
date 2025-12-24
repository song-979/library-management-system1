package org.example.model;

public class CategoryReport {
    private String category;
    private int titles;
    private int totalCopies;
    private int availableCopies;
    private int borrowCount;

    public CategoryReport(String category, int titles, int totalCopies, int availableCopies, int borrowCount) {
        this.category = category;
        this.titles = titles;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.borrowCount = borrowCount;
    }

    public String getCategory() { return category; }
    public int getTitles() { return titles; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public int getBorrowCount() { return borrowCount; }
}
