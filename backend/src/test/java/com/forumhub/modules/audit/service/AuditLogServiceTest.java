package com.forumhub.modules.audit.service;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @InjectMocks
    private AuditLogService auditLogService;

    @Mock
    private AuditLogRepository repository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should log action with correct data")
    void logAction_ShouldSaveLog() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@example.com");
        
        auditLogService.logAction("DELETE", "TOPIC", 1L, "Content", "Spam");

        verify(repository).save(argThat(log -> 
            log.getActionType().equals("DELETE") &&
            log.getEntityType().equals("TOPIC") &&
            log.getEntityId().equals(1L) &&
            log.getContent().equals("Content") &&
            log.getJustification().equals("Spam") &&
            log.getPerformedBy().equals("admin@example.com")
        ));
    }

    @Test
    @DisplayName("Should list all logs")
    void listAll_ShouldReturnLogs() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        
        List<AuditLog> result = auditLogService.listAll();
        
        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should permanently delete log")
    void permanentDelete_ShouldDeleteById() {
        auditLogService.permanentDelete(1L);
        verify(repository).deleteById(1L);
    }
}
