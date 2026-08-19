package com.PGMATE.demo.dto;

import com.PGMATE.demo.model.Payment; 
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDTO {

   
    private Integer tenancyId;
    private BigDecimal amount;
    private String paymentForMonth;
    private String status; 
    private String pgName;
    private String tenantName;
    private LocalDateTime paymentDate;

    public PaymentDTO() {}

    public PaymentDTO(Integer tenancyId, BigDecimal amount, String paymentForMonth, String status) {
        this.tenancyId = tenancyId;
        this.amount = amount;
        this.paymentForMonth = paymentForMonth;
        this.status = status;
    }

    public PaymentDTO(String pgName, LocalDateTime paymentDate, BigDecimal amount, String paymentForMonth, Payment.PaymentStatus status) { // <-- 2. Changed type to Enum
        this.pgName = pgName;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentForMonth = paymentForMonth;
        this.status = status.name(); 
    }
    
    public PaymentDTO(String pgName, String tenantName, LocalDateTime paymentDate, BigDecimal amount, String paymentForMonth, Payment.PaymentStatus status) { // <-- 4. Changed type to Enum
        this.pgName = pgName;
        this.tenantName = tenantName;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentForMonth = paymentForMonth;
        this.status = status.name(); 
    }


    public Integer getTenancyId() { return tenancyId; }
    public void setTenancyId(Integer tenancyId) { this.tenancyId = tenancyId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentForMonth() { return paymentForMonth; }
    public void setPaymentForMonth(String paymentForMonth) { this.paymentForMonth = paymentForMonth; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPgName() { return pgName; }
    public void setPgName(String pgName) { this.pgName = pgName; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}