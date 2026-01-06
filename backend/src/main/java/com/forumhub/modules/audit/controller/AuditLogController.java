package com.forumhub.modules.audit.controller;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Audit", description = "Endpoints for viewing system audit logs and recovering deleted data. Restricted to administrators.")
public class AuditLogController {
    private final AuditLogService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List audit logs", description = "Retrieves a list of all audit logs recorded by the system. Includes deletions and significant changes.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required")
        })
    public ResponseEntity<List<AuditLog>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore deleted entity", description = "Restores a previously deleted topic or answer from the audit log record. If it's a topic, you can optionally restore all its associated answers.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Entity restored successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required"),
            @ApiResponse(responseCode = "404", description = "Log entry not found")
        })
    public ResponseEntity<Void> restore(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean includeAnswers
    ) {
        service.restore(id, includeAnswers);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Purge log entry", description = "Permanently deletes a log entry. This action is irreversible.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Log entry purged successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required"),
            @ApiResponse(responseCode = "404", description = "Log entry not found")
        })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.permanentDelete(id);
        return ResponseEntity.noContent().build();
    }
}
