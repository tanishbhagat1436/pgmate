package com.PGMATE.demo.dto;

import com.PGMATE.demo.model.Booking.BookingStatus;
import java.time.LocalDateTime;

public class TenantBookingResponseDTO {
    
    private Integer bookingId;
    private String pgName;
    private String ownerName;
    private String contactPhone;
    private String contactEmail;
    private BookingStatus status;
    private LocalDateTime requestDate;

    public TenantBookingResponseDTO(Integer bookingId, String pgName, String ownerName, String contactPhone, String contactEmail, BookingStatus status, LocalDateTime requestDate) {
        this.bookingId = bookingId;
        this.pgName = pgName;
        this.ownerName = ownerName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.status = status;
        this.requestDate = requestDate;
    }

    public Integer getBookingId() { return bookingId; }
    public String getPgName() { return pgName; }
    public String getOwnerName() { return ownerName; }
    public String getContactPhone() { return contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getRequestDate() { return requestDate; }
}