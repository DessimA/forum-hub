package com.forumhub.modules.audit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "action_type")
    private String actionType;
    
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    private String content;
    private String justification;
    
    @Column(name = "performed_by")
    private String performedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
