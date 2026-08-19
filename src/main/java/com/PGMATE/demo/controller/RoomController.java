package com.PGMATE.demo.controller;

import com.PGMATE.demo.model.Pg;
import com.PGMATE.demo.model.Room;
import com.PGMATE.demo.repository.PgRepository;
import com.PGMATE.demo.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PgRepository pgRepository;

    @GetMapping("/rooms/pg/{pgId}")
    public ResponseEntity<List<Room>> getRoomsForPg(@PathVariable Integer pgId) {
        List<Room> rooms = roomRepository.findByPgPgId(pgId);
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/pg/{pgId}/available")
    public ResponseEntity<List<Room>> getAvailableRoomsForPg(@PathVariable Integer pgId) {
        List<Room> rooms = roomRepository.findByPgPgIdAndStatus(pgId, Room.RoomStatus.Available);
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody Map<String, Object> payload) {
        try {
            Integer pgId = (Integer) payload.get("pgId");
            String roomNumber = (String) payload.get("roomNumber");
            BigDecimal rent = new BigDecimal(payload.get("rent").toString());

            Pg pg = pgRepository.findById(pgId)
                    .orElseThrow(() -> new RuntimeException("PG not found with id " + pgId));

            Room newRoom = new Room();
            newRoom.setPg(pg);
            newRoom.setRoomNumber(roomNumber);
            newRoom.setRent(rent);
            newRoom.setStatus(Room.RoomStatus.Available);

            roomRepository.save(newRoom);
            return new ResponseEntity<>(newRoom, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}