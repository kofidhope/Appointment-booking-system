package com.kofi.booking_system.audit.service;

public interface AuditLogService {

    void log(String actor, String action, String entityType, Long entityId, String details);
}
