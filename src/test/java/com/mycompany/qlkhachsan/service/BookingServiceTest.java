package com.mycompany.qlkhachsan.service;

import com.mycompany.qlkhachsan.dao.BaseDAO;
import com.mycompany.qlkhachsan.model.Booking;
import com.mycompany.qlkhachsan.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BaseDAO<Booking> bookingDAO;
    @Mock
    private BaseDAO<Payment> paymentDAO;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingDAO, paymentDAO);
    }

    @Test
    void testCheckOut_Fail_WhenNotPaid() {
        int bookingId = 1;
        Payment unpaid = new Payment();
        unpaid.setBookingId(bookingId);
        unpaid.setStatus("UNPAID"); 
        
        when(paymentDAO.getAll()).thenReturn(Arrays.asList(unpaid));

        String result = bookingService.checkOut(bookingId);

        assertTrue(result.contains("CHECKOUT_FAILED"), "Loi: He thong phai tu choi Check-out khi chua hoan tat thanh toan.");
        verify(bookingDAO, never()).update(any());
    }

    @Test
    void testCheckOut_Success_WhenPaid() {
        int bookingId = 1;
        Payment paid = new Payment();
        paid.setBookingId(bookingId);
        paid.setStatus("PAID"); 

        Booking b = new Booking();
        b.setId(bookingId);
        b.setStatus("CHECKED_IN");

        when(paymentDAO.getAll()).thenReturn(Arrays.asList(paid));
        when(bookingDAO.getById(bookingId)).thenReturn(b);

        String result = bookingService.checkOut(bookingId);

        assertEquals("SUCCESS: Check-out completed.", result, "Loi: Quy trinh Check-out phai thanh cong khi da thanh toan.");
        assertEquals("CHECKED_OUT", b.getStatus());
        verify(bookingDAO, times(1)).update(b);
    }
}
