package com.mycompany.qlkhachsan.model;

import java.time.LocalDate;

public class Booking {
    private int id;
    private int customerId;
    private int roomId;
    private int staffId;
    private String status; // BOOKED, CHECKED_IN, CHECKED_OUT, CANCELLED
    private double totalAmount;
    private LocalDate bookingDate;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate expectedCheckOutDate; // Ngày checkout dự kiến
    private LocalDate actualCheckOutDate;   // Ngày checkout thực tế

    public Booking() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public LocalDate getExpectedCheckOutDate() { return expectedCheckOutDate; }
    public void setExpectedCheckOutDate(LocalDate expectedCheckOutDate) { this.expectedCheckOutDate = expectedCheckOutDate; }

    public LocalDate getActualCheckOutDate() { return actualCheckOutDate; }
    public void setActualCheckOutDate(LocalDate actualCheckOutDate) { this.actualCheckOutDate = actualCheckOutDate; }
}