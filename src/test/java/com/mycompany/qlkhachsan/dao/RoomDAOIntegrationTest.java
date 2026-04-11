package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Room;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử tích hợp cho chức năng Quản lý Phòng.
 */
public class RoomDAOIntegrationTest {
    private static RoomDAO roomDAO;

    @BeforeAll
    static void init() {
        roomDAO = new RoomDAO();
    }

    // Kiểm tra tính năng Thêm phòng mới
    @Test
    void testAddAndGetRoom() {
        Room newRoom = new Room();
        // Tạo số phòng ngẫu nhiên để không bị trùng (vì roomNumber là duy nhất)
        String testRoomNumber = "R-" + (int)(Math.random() * 1000000); 
        newRoom.setRoomNumber(testRoomNumber);
        newRoom.setType("SINGLE");
        newRoom.setPrice(50.0);
        newRoom.setStatus("EMPTY");
        newRoom.setEnable(true);

        // Lưu phòng vào database
        boolean isAdded = roomDAO.add(newRoom);
        assertTrue(isAdded, "Loi: Ham add() khong the luu phong moi vao Database.");

        // Lấy lại danh sách để chắc chắn phòng đã nằm trong đó
        List<Room> rooms = roomDAO.getAll();
        assertNotNull(rooms, "Loi: Danh sach phong tra ve bi Null.");
        
        boolean found = false;
        for (Room r : rooms) {
            if (testRoomNumber.equals(r.getRoomNumber())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Loi: Khong tim thay phong vua them trong danh sach truy van.");
    }

    // Kiểm tra tính năng Cập nhật trạng thái phòng
    @Test
    void testUpdateRoomStatus() {
        List<Room> rooms = roomDAO.getAll();
        if (rooms != null && !rooms.isEmpty()) {
            Room room = rooms.get(0);
            String oldStatus = room.getStatus();
            String testStatus = "DANG_SUA_CHUA_" + (int)(Math.random() * 100);

            // Cập nhật trạng thái mới
            room.setStatus(testStatus);
            boolean isUpdated = roomDAO.update(room);
            assertTrue(isUpdated, "Loi: Khong the cap nhat trang thai phong.");

            // Truy vấn lại từ DB xem trạng thái đã thực sự đổi chưa
            Room updatedRoom = roomDAO.getById(room.getId());
            assertEquals(testStatus, updatedRoom.getStatus(), "Loi: Trang thai trong Database khong khop sau khi cap nhat.");

            // Trả lại trạng thái ban đầu để tránh làm bẩn dữ liệu
            room.setStatus(oldStatus);
            roomDAO.update(room);
        }
    }
}
