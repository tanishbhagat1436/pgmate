package com.PGMATE.demo.repository;

import com.PGMATE.demo.dto.TenantDTO;
import com.PGMATE.demo.model.Tenancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TenancyRepository extends JpaRepository<Tenancy, Integer> {

    @Query("SELECT new com.PGMATE.demo.dto.TenantDTO(t.tenancyId, u.userId, u.name, u.email, p.pgId, p.pgName, t.moveInDate, r.rent) " +
           "FROM Tenancy t " +
           "JOIN t.room r " +
           "JOIN t.room.pg p " +
           "JOIN User u ON t.tenantId = u.userId " +
           "WHERE p.ownerId = :ownerId AND t.status = 'Active'")
    List<TenantDTO> findActiveTenantsByOwnerId(@Param("ownerId") Integer ownerId);
}