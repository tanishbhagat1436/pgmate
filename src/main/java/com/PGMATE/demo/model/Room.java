package com.PGMATE.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- 1. ADD THIS IMPORT
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id", nullable = false)
    private Pg pg;

    @Column(nullable = false)
    private String roomNumber;

    @Column(name = "rent_per_month", nullable = false)
    private BigDecimal rent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    public enum RoomStatus {
        Available,
        Occupied,
        Maintenance
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = RoomStatus.Available;
        }
    }


    public Integer getRoomId() {
        return roomId;
    }
    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
    public Pg getPg() {
        return pg;
    }
    public void setPg(Pg pg) {
        this.pg = pg;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    public BigDecimal getRent() {
        return rent;
    }
    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }
    public RoomStatus getStatus() {
        return status;
    }
    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}