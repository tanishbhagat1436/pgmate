package com.PGMATE.demo.controller;

import com.PGMATE.demo.dto.PaymentDTO;
import com.PGMATE.demo.model.Payment;
import com.PGMATE.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/payments/record")
    public ResponseEntity<?> recordPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            Payment payment = new Payment();
            payment.setTenancyId(paymentDTO.getTenancyId());
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentForMonth(paymentDTO.getPaymentForMonth());
            payment.setStatus(Payment.PaymentStatus.Success); 
            
            paymentRepository.save(payment);
            return new ResponseEntity<>("Payment recorded successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to record payment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/payments/tenant/{tenantId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForTenant(@PathVariable Integer tenantId) {
        List<PaymentDTO> payments = paymentRepository.findPaymentsByTenantId(tenantId);
        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/payments/owner/{ownerId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsForOwner(@PathVariable Integer ownerId) {
        List<PaymentDTO> payments = paymentRepository.findPaymentsByOwnerId(ownerId);
        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }
}