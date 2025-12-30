package com.forumhub.modules.audit.service;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock private AuditLogRepository repository;
    @InjectMocks private AuditLogService service;

    @Test
    @DisplayName("Should save audit log correctly")
    void shouldSaveLog() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UUID entityId = UUID.randomUUID();
        service.logAction("DELETE", "TOPIC", entityId, "Content", "Justification");

        verify(repository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should delete log entry")
    void shouldDeleteLog() {
        UUID logId = UUID.randomUUID();
        service.permanentDelete(logId);
        verify(repository).deleteById(logId);
    }
}