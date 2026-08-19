package com.PGMATE.demo.repository;

import com.PGMATE.demo.dto.BookingResponseDTO;
import com.PGMATE.demo.dto.TenantBookingResponseDTO; 
import com.PGMATE.demo.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT new com.PGMATE.demo.dto.BookingResponseDTO(b.bookingId, p.pgId, p.pgName, u.userId, u.name, u.email, b.status, b.requestDate) " +
           "FROM Booking b " +
           "JOIN Pg p ON b.pgId = p.pgId " +
           "JOIN User u ON b.tenantId = u.userId " +
           "WHERE b.ownerId = :ownerId " +
           "ORDER BY b.requestDate DESC")
    List<BookingResponseDTO> findBookingsByOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT new com.PGMATE.demo.dto.TenantBookingResponseDTO(b.bookingId, p.pgName, o.name, p.contactPhone, p.contactEmail, b.status, b.requestDate) " +
           "FROM Booking b " +
           "JOIN Pg p ON b.pgId = p.pgId " +
           "JOIN User o ON b.ownerId = o.userId " +
           "WHERE b.tenantId = :tenantId " +
           "ORDER BY b.requestDate DESC")
    List<TenantBookingResponseDTO> findBookingsByTenantId(@Param("tenantId") Integer tenantId);
}