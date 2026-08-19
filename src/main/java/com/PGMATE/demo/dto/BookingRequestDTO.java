package com.PGMATE.demo.dto;

public class BookingRequestDTO {
    
    private Integer pgId;
    private Integer tenantId;
    private Integer ownerId;
    
    public Integer getPgId() { return pgId; }
    public void setPgId(Integer pgId) { this.pgId = pgId; }
    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
}