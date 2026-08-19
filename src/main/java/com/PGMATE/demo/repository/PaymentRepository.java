package com.PGMATE.demo.repository;

import com.PGMATE.demo.dto.PaymentDTO;
import com.PGMATE.demo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT new com.PGMATE.demo.dto.PaymentDTO(p.pgName, pay.paymentDate, pay.amount, pay.paymentForMonth, pay.status) " +
           "FROM Payment pay " +
           "JOIN Tenancy t ON pay.tenancyId = t.tenancyId " +
           "JOIN t.room r " +  
           "JOIN r.pg p " +    
           "WHERE t.tenantId = :tenantId " +
           "ORDER BY pay.paymentDate DESC")
    List<PaymentDTO> findPaymentsByTenantId(@Param("tenantId") Integer tenantId);

    @Query("SELECT new com.PGMATE.demo.dto.PaymentDTO(p.pgName, u.name, pay.paymentDate, pay.amount, pay.paymentForMonth, pay.status) " +
           "FROM Payment pay " +
           "JOIN Tenancy t ON pay.tenancyId = t.tenancyId " +
           "JOIN t.room r " +  
           "JOIN r.pg p " +               "JOIN User u ON t.tenantId = u.userId " +
           "WHERE p.ownerId = :ownerId " +
           "ORDER BY pay.paymentDate DESC")
    List<PaymentDTO> findPaymentsByOwnerId(@Param("ownerId") Integer ownerId);
}