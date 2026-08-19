package com.PGMATE.demo.dto;

import java.time.LocalDateTime;

public class FeedbackDTO {
    
    private Integer pgId;
    private Integer tenantId;
    private Integer ownerId;
    private Integer rating;
    private String comment;

    private String pgName;
    private String tenantName;
    private LocalDateTime createdAt;

    public FeedbackDTO() {
    }

    public FeedbackDTO(Integer pgId, Integer tenantId, Integer ownerId, Integer rating, String comment) {
        this.pgId = pgId;
        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.rating = rating;
        this.comment = comment;
    }

    public FeedbackDTO(String pgName, String tenantName, Integer rating, String comment, LocalDateTime createdAt) {
        this.pgName = pgName;
        this.tenantName = tenantName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Integer getPgId() { return pgId; }
    public void setPgId(Integer pgId) { this.pgId = pgId; }
    
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getPgName() { return pgName; }
    public String getTenantName() { return tenantName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}