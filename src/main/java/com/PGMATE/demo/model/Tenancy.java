package com.PGMATE.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tenancies")
public class Tenancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tenancyId;

    private Integer tenantId;
    private Integer pgId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private LocalDate moveInDate;
    private LocalDate moveOutDate;

    @Enumerated(EnumType.STRING)
    private TenancyStatus status;

    public enum TenancyStatus {
        Active,
        Inactive
    }

    @PrePersist
    protected void onCreate() {
        moveInDate = LocalDate.now();
        status = TenancyStatus.Active;
    }
    
    
    public Integer getTenancyId() { return tenancyId; }
    public void setTenancyId(Integer tenancyId) { this.tenancyId = tenancyId; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public Integer getPgId() { return pgId; }
    public void setPgId(Integer pgId) { this.pgId = pgId; }

    public LocalDate getMoveInDate() { return moveInDate; }
    public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }

    public LocalDate getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(LocalDate moveOutDate) { this.moveOutDate = moveOutDate; }

    public TenancyStatus getStatus() { return status; }
    public void setStatus(TenancyStatus status) { this.status = status; }

    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
}