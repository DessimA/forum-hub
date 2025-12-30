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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Audit", description = "System audit logs and data recovery")
public class AuditLogController {
    private final AuditLogService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all audit logs (Admin only)")
    public ResponseEntity<List<AuditLog>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore deleted entity from log (Admin only)")
    public ResponseEntity<Void> restore(@PathVariable UUID id) {
        service.restore(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Permanently delete log entry (Admin only)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.permanentDelete(id);
        return ResponseEntity.noContent().build();
    }
}
