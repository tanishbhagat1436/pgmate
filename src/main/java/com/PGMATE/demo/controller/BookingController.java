package com.PGMATE.demo.controller;

import com.PGMATE.demo.dto.BookingRequestDTO;
import com.PGMATE.demo.dto.BookingResponseDTO;
import com.PGMATE.demo.dto.TenantBookingResponseDTO;
import com.PGMATE.demo.model.Booking;
import com.PGMATE.demo.model.Room;  
import com.PGMATE.demo.model.Tenancy;
import com.PGMATE.demo.repository.BookingRepository;
import com.PGMATE.demo.repository.RoomRepository; 
import com.PGMATE.demo.repository.TenancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TenancyRepository tenancyRepository;

    @Autowired
    private RoomRepository roomRepository; 

    
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest) {
        try {
            Booking newBooking = new Booking();
            newBooking.setPgId(bookingRequest.getPgId());
            newBooking.setTenantId(bookingRequest.getTenantId());
            newBooking.setOwnerId(bookingRequest.getOwnerId());
            
            bookingRepository.save(newBooking);
            return new ResponseEntity<>("Booking request sent successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send booking request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bookings/owner/{ownerId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForOwner(@PathVariable Integer ownerId) {
        List<BookingResponseDTO> bookings = bookingRepository.findBookingsByOwnerId(ownerId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/bookings/tenant/{tenantId}")
    public ResponseEntity<List<TenantBookingResponseDTO>> getBookingsForTenant(@PathVariable Integer tenantId) {
        List<TenantBookingResponseDTO> bookings = bookingRepository.findBookingsByTenantId(tenantId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/bookings/{bookingId}")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Integer bookingId, @RequestBody Map<String, Object> payload) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            
            String status = (String) payload.get("status");
            
            if ("Approved".equals(status)) {
                Integer roomId = (Integer) payload.get("roomId");
                if (roomId == null) {
                    throw new RuntimeException("Room ID is required to approve a booking.");
                }

                Room assignedRoom = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
                
                if (assignedRoom.getStatus() != Room.RoomStatus.Available) {
                    throw new RuntimeException("Selected room is no longer available.");
                }

                booking.setStatus(Booking.BookingStatus.Approved);
                
                Tenancy newTenancy = new Tenancy();
                newTenancy.setTenantId(booking.getTenantId());
                newTenancy.setPgId(booking.getPgId());
                
                newTenancy.setRoom(assignedRoom);
                
                tenancyRepository.save(newTenancy);
                
                assignedRoom.setStatus(Room.RoomStatus.Occupied);
                roomRepository.save(assignedRoom);

            } else if ("Rejected".equals(status)) {
                booking.setStatus(Booking.BookingStatus.Rejected);
            } else {
                return new ResponseEntity<>("Invalid status", HttpStatus.BAD_REQUEST);
            }
            
            bookingRepository.save(booking);
            return ResponseEntity.ok("Booking status updated");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}