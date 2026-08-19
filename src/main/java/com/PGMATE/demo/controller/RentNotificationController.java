package com.PGMATE.demo.controller;

import com.PGMATE.demo.dto.RentNotificationDTO;
import com.PGMATE.demo.model.RentNotification;
import com.PGMATE.demo.repository.RentNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RentNotificationController {

    @Autowired
    private RentNotificationRepository notificationRepository;

    @PostMapping("/notifications")
    public ResponseEntity<?> sendNotification(@RequestBody RentNotificationDTO notificationDTO) {
        try {
            RentNotification notification = new RentNotification();
            notification.setOwnerId(notificationDTO.getOwnerId());
            notification.setTenantId(notificationDTO.getTenantId());
            notification.setPgId(notificationDTO.getPgId());
            notification.setMessage(notificationDTO.getMessage());
            
            notificationRepository.save(notification);
            return new ResponseEntity<>("Notification sent", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send notification", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/notifications/tenant/{tenantId}")
    public ResponseEntity<List<RentNotificationDTO>> getNotificationsForTenant(@PathVariable Integer tenantId) {
        List<RentNotificationDTO> notifications = notificationRepository.findNotificationsByTenantId(tenantId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/owner/{ownerId}")
    public ResponseEntity<List<RentNotificationDTO>> getNotificationsForOwner(@PathVariable Integer ownerId) {
        List<RentNotificationDTO> notifications = notificationRepository.findNotificationsByOwnerId(ownerId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer notificationId) {
        try {
            RentNotification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new RuntimeException("Notification not found"));
            
            notification.setStatus(RentNotification.NotificationStatus.Read);
            notificationRepository.save(notification);
            return ResponseEntity.ok("Marked as read");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}