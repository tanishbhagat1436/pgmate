package com.PGMATE.demo.controller;

import com.PGMATE.demo.dto.TenantDTO;
import com.PGMATE.demo.repository.TenancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TenancyController {

    @Autowired
    private TenancyRepository tenancyRepository;

    @GetMapping("/tenancies/owner/{ownerId}")
    public ResponseEntity<List<TenantDTO>> getActiveTenantsForOwner(@PathVariable Integer ownerId) {
        List<TenantDTO> tenants = tenancyRepository.findActiveTenantsByOwnerId(ownerId);
        if (tenants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tenants);
    }
}