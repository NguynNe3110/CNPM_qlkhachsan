package com.mycompany.qlkhachsan.service;

import com.mycompany.qlkhachsan.model.ServiceUsage;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceTest {

    private final InvoiceService invoiceService = new InvoiceService();

    @Test
    void testCalculateTotalServiceAmount() {
        ServiceUsage usage1 = new ServiceUsage();
        usage1.setPrice(10.0);
        usage1.setQuantity(2);
        
        ServiceUsage usage2 = new ServiceUsage();
        usage2.setPrice(5.0);
        usage2.setQuantity(3);
        
        List<ServiceUsage> list = Arrays.asList(usage1, usage2);

        double total = invoiceService.calculateTotalServiceAmount(list);
        assertEquals(35.0, total, "Loi: Tong tien dich vu tinh toan sai lech so voi ky vong.");
    }

    @Test
    void testCalculateRoomAmount_MultipleNights() {
        double amount = invoiceService.calculateRoomAmount(100.0, 3);
        assertEquals(300.0, amount, "Loi: Tong tien phong tinh toan sai lech (3 dem x 100/dem).");
    }

    @Test
    void testCalculateRoomAmount_ZeroNights() {
        double amount = invoiceService.calculateRoomAmount(100.0, 0);
        assertEquals(100.0, amount, "Loi: He thong phai tinh tron 1 ngay cho truong hop o duoi 24h.");
    }
}
