package com.forumhub.modules.audit.controller;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Audit Logs", description = "System-wide activity logs (Admin only)")
public class AuditLogController {
    private final AuditLogService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all audit logs")
    public ResponseEntity<List<AuditLog>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Permanently delete a log entry and its archived content")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.permanentDelete(id);
        return ResponseEntity.noContent().build();
    }
}
