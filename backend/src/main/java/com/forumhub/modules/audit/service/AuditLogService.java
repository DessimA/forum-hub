package com.forumhub.modules.audit.service;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(String actionType, String entityType, Long entityId, String content, String justification) {
        var performer = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Logging audit action: {} on {} ID: {} by {}", actionType, entityType, entityId, performer);
        
        var auditLog = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .content(content)
                .justification(justification)
                .performedBy(performer)
                .build();
        
        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> listAll() {
        log.debug("Listing all audit logs");
        return auditLogRepository.findAll();
    }

    @Transactional
    public void permanentDelete(Long logId) {
        log.warn("Permanently deleting audit log ID: {}", logId);
        auditLogRepository.deleteById(logId);
    }
}