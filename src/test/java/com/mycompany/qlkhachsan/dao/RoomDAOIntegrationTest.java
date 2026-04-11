package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Room;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RoomDAOIntegrationTest {
    private static RoomDAO roomDAO;

    @BeforeAll
    static void init() {
        roomDAO = new RoomDAO();
    }

    @Test
    void testAddAndGetRoom() {
        Room newRoom = new Room();
        String testRoomNumber = "R-" + (int)(Math.random() * 1000000); 
        newRoom.setRoomNumber(testRoomNumber);
        newRoom.setType("SINGLE");
        newRoom.setPrice(50.0);
        newRoom.setStatus("EMPTY");
        newRoom.setEnable(true);

        boolean isAdded = roomDAO.add(newRoom);
        assertTrue(isAdded, "Loi: Ham add() khong the luu phong moi vao Database.");

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

    @Test
    void testUpdateRoomStatus() {
        List<Room> rooms = roomDAO.getAll();
        if (rooms != null && !rooms.isEmpty()) {
            Room room = rooms.get(0);
            String oldStatus = room.getStatus();
            String testStatus = "BUSY_" + (int)(Math.random() * 100);

            room.setStatus(testStatus);
            boolean isUpdated = roomDAO.update(room);
            assertTrue(isUpdated, "Loi: Khong the cap nhat trang thai phong.");

            Room updatedRoom = roomDAO.getById(room.getId());
            assertEquals(testStatus, updatedRoom.getStatus(), "Loi: Trang thai trong Database khong khop sau khi cap nhat.");

            room.setStatus(oldStatus);
            roomDAO.update(room);
        }
    }
}
