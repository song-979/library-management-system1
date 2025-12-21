package org.example.model;

public class Reader {
    private int id;
    private String name;
    private String code;
    private String phone;
    private int maxBorrow;
    private String status;
    private String createdTime;
    private String updatedTime;

    public Reader(String name, String code, String phone, int maxBorrow, String status) {
        this.name = name;
        this.code = code;
        this.phone = phone;
        this.maxBorrow = maxBorrow;
        this.status = status;
    }

    public Reader(int id, String name, String code, String phone, int maxBorrow, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.phone = phone;
        this.maxBorrow = maxBorrow;
        this.status = status;
        this.createdTime = createdAt;
        this.updatedTime = updatedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getPhone() { return phone; }
    public int getMaxBorrow() { return maxBorrow; }
    public String getStatus() { return status; }
    public String getCreatedTime() { return createdTime; }
    public String getUpdatedTime() { return updatedTime; }
}
