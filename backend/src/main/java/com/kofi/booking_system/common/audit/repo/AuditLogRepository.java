package com.kofi.booking_system.common.audit.repo;

import com.kofi.booking_system.common.audit.Entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    Page<AuditLog> findAllByActor(String actor, Pageable pageable);

    Page<AuditLog> findAllByEntityTypeAndEntityId(String entityType, Long entityId,Pageable pageable);

}
