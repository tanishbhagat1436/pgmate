package com.PGMATE.demo.repository;

import com.PGMATE.demo.dto.RentNotificationDTO;
import com.PGMATE.demo.model.RentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentNotificationRepository extends JpaRepository<RentNotification, Integer> {

    @Query("SELECT new com.PGMATE.demo.dto.RentNotificationDTO(n.notificationId, p.pgName, n.message, n.status, n.createdAt) " + // <-- Added n.notificationId
           "FROM RentNotification n " +
           "JOIN Pg p ON n.pgId = p.pgId " +
           "WHERE n.tenantId = :tenantId " +
           "ORDER BY n.createdAt DESC")
    List<RentNotificationDTO> findNotificationsByTenantId(@Param("tenantId") Integer tenantId);

    @Query("SELECT new com.PGMATE.demo.dto.RentNotificationDTO(u.name, p.pgName, n.message, n.status, n.createdAt) " +
           "FROM RentNotification n " +
           "JOIN User u ON n.tenantId = u.userId " +
           "JOIN Pg p ON n.pgId = p.pgId " +
           "WHERE n.ownerId = :ownerId " +
           "ORDER BY n.createdAt DESC")
    List<RentNotificationDTO> findNotificationsByOwnerId(@Param("ownerId") Integer ownerId);
}