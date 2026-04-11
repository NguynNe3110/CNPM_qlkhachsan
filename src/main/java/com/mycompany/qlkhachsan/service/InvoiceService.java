package com.mycompany.qlkhachsan.service;

import com.mycompany.qlkhachsan.model.ServiceUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tính toán hóa đơn (Dùng để làm Unit Test logic tiền tệ)
 */
public class InvoiceService {
    
    /**
     * Tính tổng số tiền dịch vụ
     * Công thức: Tổng (Giá * Số lượng)
     */
    public double calculateTotalServiceAmount(List<ServiceUsage> usages) {
        if (usages == null || usages.isEmpty()) return 0.0;
        
        double total = 0;
        for (ServiceUsage usage : usages) {
            total += usage.getPrice() * usage.getQuantity();
        }
        return total;
    }
    
    /**
     * Tính tiền phòng dựa trên số ngày ở
     */
    public double calculateRoomAmount(double pricePerNight, int nights) {
        if (nights <= 0) return pricePerNight; // Tính 1 ngày nếu check-out sớm
        return pricePerNight * nights;
    }
}
