package com.forumhub.modules.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for Unread Notifications Count")
public record UnreadCountResponseDTO(
    @Schema(description = "Number of unread notifications")
    long count
) {}
