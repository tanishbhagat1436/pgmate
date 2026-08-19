package com.PGMATE.demo.dto;

import com.PGMATE.demo.model.RentNotification.NotificationStatus;
import java.time.LocalDateTime;

public class RentNotificationDTO {
    
    private Integer notificationId; 
    
    private Integer ownerId;
    private Integer tenantId;
    private Integer pgId;
    private String message;

    private String tenantName;
    private String pgName;
    private NotificationStatus status;
    private LocalDateTime createdAt;

    public RentNotificationDTO() {
    }

    public RentNotificationDTO(Integer ownerId, Integer tenantId, Integer pgId, String message) {
        this.ownerId = ownerId;
        this.tenantId = tenantId;
        this.pgId = pgId;
        this.message = message;
    }

    public RentNotificationDTO(Integer notificationId, String pgName, String message, NotificationStatus status, LocalDateTime createdAt) {
        this.notificationId = notificationId; 
        this.pgName = pgName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public RentNotificationDTO(String tenantName, String pgName, String message, NotificationStatus status, LocalDateTime createdAt) {
        this.tenantName = tenantName;
        this.pgName = pgName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }


    public Integer getNotificationId() { return notificationId; } 
    public Integer getOwnerId() { return ownerId; }
    public Integer getTenantId() { return tenantId; }
    public Integer getPgId() { return pgId; }
    public String getMessage() { return message; }
    public String getTenantName() { return tenantName; }
    public String getPgName() { return pgName; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
    public void setPgId(Integer pgId) {
        this.pgId = pgId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}