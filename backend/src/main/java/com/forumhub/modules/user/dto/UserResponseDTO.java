package com.forumhub.modules.user.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    String profile,
    LocalDateTime createdAt
) {}
