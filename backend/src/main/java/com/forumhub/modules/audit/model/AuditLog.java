package com.forumhub.modules.audit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String actionType; // DELETE, BLOCK, etc

    @Column(nullable = false)
    private String entityType; // TOPIC, ANSWER, USER

    @Column(nullable = false)
    private String entityId; // Guardamos como String o UUID da entidade afetada

    @Column(columnDefinition = "TEXT")
    private String content; // JSON ou texto serializado do objeto deletado

    private String justification;

    @Column(nullable = false)
    private String performedBy; // Email do autor da ação

    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
