package com.forumhub.modules.audit.repository;

import com.forumhub.modules.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
