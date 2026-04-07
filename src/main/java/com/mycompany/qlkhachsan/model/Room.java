package com.mycompany.qlkhachsan.model;

public class Room {
    private int id;
    private String roomNumber;
    private String type; // SINGLE, DOUBLE, etc.
    private String status; // EMPTY, OCCUPIED, MAINTENANCE
    private double price;
    private boolean enable;

    public Room() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }
}
