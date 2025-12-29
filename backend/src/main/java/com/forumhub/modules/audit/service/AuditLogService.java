package com.forumhub.modules.audit.service;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repository;

    @Transactional
    public void logAction(String actionType, String entityType, Long entityId, String content, String justification) {
        var user = SecurityContextHolder.getContext().getAuthentication().getName();
        var log = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId)
                .content(content)
                .justification(justification)
                .performedBy(user)
                .build();
        repository.save(log);
    }

    public List<AuditLog> listAll() { return repository.findAll(); }

    @Transactional
    public void permanentDelete(Long logId) { repository.deleteById(logId); }
}
