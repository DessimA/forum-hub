package com.forumhub.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User response data")
public record UserResponseDTO(
    @Schema(description = "User's unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "User's name", example = "John Doe")
    String name,

    @Schema(description = "User's email", example = "john.doe@example.com")
    String email,

    @Schema(description = "User's profile/role", example = "ROLE_USER")
    String profile,

    @Schema(description = "Account creation date")
    LocalDateTime createdAt,

    @Schema(description = "Indicates if the user is permanently blocked", example = "false")
    Boolean isPermanentBlocked,

    @Schema(description = "Date until which the user is blocked (if applicable)")
    LocalDateTime blockedUntil
) {}
