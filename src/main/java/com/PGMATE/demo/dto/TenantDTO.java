package com.PGMATE.demo.dto;

import java.time.LocalDate;
import java.math.BigDecimal; 

public class TenantDTO {
    
    private Integer tenancyId;
    private Integer tenantId;
    private String tenantName;
    private String tenantEmail;
    private Integer pgId;
    private String pgName;
    private LocalDate moveInDate;
    private BigDecimal rentPerMonth; 

    public TenantDTO(Integer tenancyId, Integer tenantId, String tenantName, String tenantEmail, Integer pgId, String pgName, LocalDate moveInDate, BigDecimal rentPerMonth) {
        this.tenancyId = tenancyId;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantEmail = tenantEmail;
        this.pgId = pgId;
        this.pgName = pgName;
        this.moveInDate = moveInDate;
        this.rentPerMonth = rentPerMonth;
    }

    public Integer getTenancyId() { return tenancyId; }
    public Integer getTenantId() { return tenantId; }
    public String getTenantName() { return tenantName; }
    public String getTenantEmail() { return tenantEmail; }
    public Integer getPgId() { return pgId; }
    public String getPgName() { return pgName; }
    public LocalDate getMoveInDate() { return moveInDate; }
    public BigDecimal getRentPerMonth() { return rentPerMonth; }
}