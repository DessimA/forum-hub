package com.forumhub.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the JWT token")
public record TokenResponseDTO(
    @Schema(description = "JWT authentication token")
    String token
) {}