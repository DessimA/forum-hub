package com.forumhub.modules.audit.repository;

import com.forumhub.modules.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findAllByActionTypeAndEntityTypeAndContentContaining(String actionType, String entityType, String contentFragment);
}