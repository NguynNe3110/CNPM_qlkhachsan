package com.mycompany.qlkhachsan.service;

import com.mycompany.qlkhachsan.dao.BaseDAO;
import com.mycompany.qlkhachsan.model.Booking;
import com.mycompany.qlkhachsan.model.Payment;
import java.util.List;

public class BookingService {
    private final BaseDAO<Booking> bookingDAO;
    private final BaseDAO<Payment> paymentDAO;

    public BookingService(BaseDAO<Booking> bDao, BaseDAO<Payment> pDao) {
        this.bookingDAO = bDao;
        this.paymentDAO = pDao;
    }

    /**
     * Logic: Check-out only if payment exists and is PAID status.
     * Requirement: phải thanh toán mới checkout được, k thì chặn k cho checkout
     */
    public String checkOut(int bookingId) {
        boolean isPaid = false;
        List<Payment> allPayments = paymentDAO.getAll();
        for (Payment p : allPayments) {
            if (p.getBookingId() == bookingId && "PAID".equals(p.getStatus())) {
                isPaid = true;
                break;
            }
        }

        if (!isPaid) {
            return "CHECKOUT_FAILED: Payment required before checkout.";
        }

        Booking b = bookingDAO.getById(bookingId);
        if (b != null) {
            b.setStatus("CHECKED_OUT");
            bookingDAO.update(b);
            return "SUCCESS: Check-out completed.";
        }

        return "ERROR: Booking not found.";
    }

    public boolean checkIn(int bookingId) {
        Booking b = bookingDAO.getById(bookingId);
        if (b != null) {
            b.setStatus("CHECKED_IN");
            return bookingDAO.update(b);
        }
        return false;
    }

    /**
     * Admin Report: Total Revenue
     */
    public double getTotalRevenue() {
        double total = 0;
        for (Payment p : paymentDAO.getAll()) {
            if ("PAID".equals(p.getStatus())) {
                total += p.getAmount();
            }
        }
        return total;
    }

    /**
     * Admin Report: Room usage frequency (count of bookings per room)
     */
    public java.util.Map<Integer, Integer> getRoomUsageFrequency() {
        java.util.Map<Integer, Integer> frequency = new java.util.HashMap<>();
        for (Booking b : bookingDAO.getAll()) {
            frequency.put(b.getRoomId(), frequency.getOrDefault(b.getRoomId(), 0) + 1);
        }
        return frequency;
    }
}
