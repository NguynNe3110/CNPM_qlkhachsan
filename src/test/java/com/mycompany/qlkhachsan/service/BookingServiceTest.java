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

/**
 * Kiểm thử đơn vị (Unit Test) cho các logic đặt phòng.
 */
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    // Tạo các đối tượng giả (Mock) để không cần gọi Database thật
    @Mock
    private BaseDAO<Booking> bookingDAO;
    @Mock
    private BaseDAO<Payment> paymentDAO;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingDAO, paymentDAO);
    }

    // Kiểm tra: Khách CHƯA thanh toán thì không được Check-out
    @Test
    void testCheckOut_Fail_WhenNotPaid() {
        int bookingId = 1;
        Payment unpaid = new Payment();
        unpaid.setBookingId(bookingId);
        unpaid.setStatus("UNPAID"); // Trạng thái: Chưa trả tiền
        
        // Giả lập: Khi hệ thống hỏi danh sách thanh toán, trả về hóa đơn chưa trả tiền
        when(paymentDAO.getAll()).thenReturn(Arrays.asList(unpaid));

        // Thực hiện hành động trả phòng
        String result = bookingService.checkOut(bookingId);

        // Khẳng định: Kết quả trả về phải chứa thông báo thất bại
        assertTrue(result.contains("CHECKOUT_FAILED"), "Loi: He thong phai tu choi Check-out khi chua hoan tat thanh toan.");
        // Đảm bảo hàm cập nhật Database không được gọi (vì còn nợ tiền)
        verify(bookingDAO, never()).update(any());
    }

    // Kiểm tra: Khách ĐÃ thanh toán thì được Check-out thành công
    @Test
    void testCheckOut_Success_WhenPaid() {
        int bookingId = 1;
        Payment paid = new Payment();
        paid.setBookingId(bookingId);
        paid.setStatus("PAID"); // Trạng thái: Đã trả tiền xong

        Booking b = new Booking();
        b.setId(bookingId);
        b.setStatus("CHECKED_IN");

        // Giả lập: Trả về kết quả là đã thanh toán thành công
        when(paymentDAO.getAll()).thenReturn(Arrays.asList(paid));
        when(bookingDAO.getById(bookingId)).thenReturn(b);

        // Thực hiện trả phòng
        String result = bookingService.checkOut(bookingId);

        // Khẳng định: Phải có thông báo thành công
        assertEquals("SUCCESS: Check-out completed.", result, "Loi: Quy trinh Check-out phai thanh cong khi da thanh toan.");
        // Trạng thái đơn đặt phòng phải chuyển thành CHECKED_OUT
        assertEquals("CHECKED_OUT", b.getStatus());
        // Xác nhận hàm update Database được gọi đúng 1 lần
        verify(bookingDAO, times(1)).update(b);
    }
}
