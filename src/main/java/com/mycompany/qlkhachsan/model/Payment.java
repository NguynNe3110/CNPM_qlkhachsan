package com.mycompany.qlkhachsan.model;

public class Payment {
    private int id;
    private int bookingId;
    private double amount;
    private String method; // CASH, CARD, TRANSFER
    private String status; // PAID, UNPAID

    public Payment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
