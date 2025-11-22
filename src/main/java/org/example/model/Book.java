package org.example.model;

public class Book {
    private int id;
    private String title;
    private String category;
    private int totalCopies;
    private int availableCopies;
    private String remarks;
    private String createdTime;
    private String updatedTime;

    // 新增/编辑用构造器（不含id、时间字段，由数据库生成）
    public Book(String title, String category, int totalCopies, int availableCopies, String remarks) {
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.remarks = remarks;
    }

    // 查询用构造器（包含所有字段）
    public Book(int id, String title, String category, int totalCopies, int availableCopies, String remarks, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.remarks = remarks;
        this.createdTime = createdAt;
        this.updatedTime = updatedAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public String getRemarks() { return remarks; }
    public String getCreatedTime() { return createdTime; }
    public String getUpdatedTime() { return updatedTime; }
}