package com.PGMATE.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    private Integer tenancyId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentForMonth; 

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public enum PaymentStatus {
        Success,
        Failed,
        Pending
    }

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
    
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public Integer getTenancyId() { return tenancyId; }
    public void setTenancyId(Integer tenancyId) { this.tenancyId = tenancyId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentForMonth() { return paymentForMonth; }
    public void setPaymentForMonth(String paymentForMonth) { this.paymentForMonth = paymentForMonth; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}