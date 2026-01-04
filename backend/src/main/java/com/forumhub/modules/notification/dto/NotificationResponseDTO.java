package com.forumhub.modules.notification.dto;

import com.forumhub.modules.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO for User Notifications")
public record NotificationResponseDTO(
    @Schema(description = "Notification ID")
    UUID id,
    
    @Schema(description = "Related Topic ID")
    UUID topicId,
    
    @Schema(description = "Related Topic Title")
    String topicTitle,
    
    @Schema(description = "Type of notification")
    NotificationType notificationType,
    
    @Schema(description = "Notification content message")
    String content,
    
    @Schema(description = "Whether the notification has been read")
    Boolean isRead,
    
    @Schema(description = "Timestamp of creation")
    LocalDateTime createdAt
) {}
