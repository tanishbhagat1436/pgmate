package com.PGMATE.demo.dto;

import com.PGMATE.demo.model.Booking.BookingStatus;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    
    private Integer bookingId;
    private Integer pgId;
    private String pgName;
    private Integer tenantId;
    private String tenantName;
    private String tenantEmail;
    private BookingStatus status;
    private LocalDateTime requestDate;

    public BookingResponseDTO(Integer bookingId, Integer pgId, String pgName, Integer tenantId, String tenantName, String tenantEmail, BookingStatus status, LocalDateTime requestDate) {
        this.bookingId = bookingId;
        this.pgId = pgId;
        this.pgName = pgName;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantEmail = tenantEmail;
        this.status = status;
        this.requestDate = requestDate;
    }

    public Integer getBookingId() { return bookingId; }
    public Integer getPgId() { return pgId; }
    public String getPgName() { return pgName; }
    public Integer getTenantId() { return tenantId; }
    public String getTenantName() { return tenantName; }
    public String getTenantEmail() { return tenantEmail; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getRequestDate() { return requestDate; }
}