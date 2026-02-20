package com.kofi.booking_system.audit.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores important actions performed in the system
 * Example: CONFIRM_APPOINTMENT, CANCEL_APPOINTMENT, REJECT_APPOINTMENT
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Who performed the action (email / username)
     */
    private String actor;

    /**
     * What action was performed
     */
    private String action;

    /**
     * Which entity was affected (Appointment, User, etc.)
     */
    private String entityType;

    /**
     * ID of the entity affected
     */
    private Long entityId;

    /**
     * Extra human-readable info
     */
    @Column(length = 1000)
    private String details;

    /**
     * When it happened
     */
    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
