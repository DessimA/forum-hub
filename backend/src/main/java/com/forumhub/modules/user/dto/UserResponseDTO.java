package com.forumhub.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String name,
    String email,
    String profile,
    java.time.LocalDateTime createdAt,
    Boolean isPermanentBlocked,
    java.time.LocalDateTime blockedUntil
) {}