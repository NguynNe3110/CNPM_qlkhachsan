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
 * Kiểm thử đơn vị (Unit Test) cho BookingService.
 * Sử dụng Mockito để giả lập (Mock) các lớp DAO, giúp kiểm tra logic nghiệp vụ mà không cần Database.
 */
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BaseDAO<Booking> bookingDAO;
    @Mock
    private BaseDAO<Payment> paymentDAO;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        // Khởi tạo service với các đối tượng giả (Mock)
        bookingService = new BookingService(bookingDAO, paymentDAO);
    }

    /**
     * Kiểm tra: Không cho phép Check-out nếu khách chưa thanh toán (UNPAID)
     */
    @Test
    void testCheckOut_Fail_WhenNotPaid() {
        // Giả lập dữ liệu: Booking số 1 có bản ghi thanh toán nhưng trạng thái là 'UNPAID'
        int bookingId = 1;
        Payment unpaid = new Payment();
        unpaid.setBookingId(bookingId);
        unpaid.setStatus("UNPAID");
        
        // Khi gọi paymentDAO.getAll(), Robot sẽ trả về danh sách có đơn chưa thanh toán này
        when(paymentDAO.getAll()).thenReturn(Arrays.asList(unpaid));

        // Thực hiện hành động Check-out
        String result = bookingService.checkOut(bookingId);

        // Kiểm chứng: Kết quả phải chứa thông báo lỗi thất bại
        assertTrue(result.contains("CHECKOUT_FAILED"), "Phải báo lỗi khi chưa thanh toán");
        
        // Đảm bảo rằng hàm cập nhật trạng thái trong Database chưa bao giờ được gọi
        verify(bookingDAO, never()).update(any());
    }

    /**
     * Kiểm tra: Cho phép Check-out khi khách đã thanh toán (PAID)
     */
    @Test
    void testCheckOut_Success_WhenPaid() {
        // Giả lập dữ liệu: Đơn đã thanh toán
        int bookingId = 1;
        Payment paid = new Payment();
        paid.setBookingId(bookingId);
        paid.setStatus("PAID");

        Booking b = new Booking();
        b.setId(bookingId);
        b.setStatus("CHECKED_IN");

        // Thiết lập hành vi cho Robot Mock: Trả về kết quả 'Đã thanh toán'
        when(paymentDAO.getAll()).thenReturn(Arrays.asList(paid));
        when(bookingDAO.getById(bookingId)).thenReturn(b);

        // Thực hiện Check-out
        String result = bookingService.checkOut(bookingId);

        // Kiểm chứng: Phải trả về thông báo thành công
        assertEquals("SUCCESS: Check-out completed.", result);
        // Trạng thái của đối tượng Booking trong bộ nhớ phải đổi thành 'CHECKED_OUT'
        assertEquals("CHECKED_OUT", b.getStatus());
        // Xác nhận rằng hàm update của DAO đã được gọi đúng 1 lần
        verify(bookingDAO, times(1)).update(b);
    }
}
