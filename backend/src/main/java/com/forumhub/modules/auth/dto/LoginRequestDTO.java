package com.forumhub.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data for user authentication")
public record LoginRequestDTO(
    @Schema(description = "User's email", example = "user@example.com")
    @NotBlank @Email String email,

    @Schema(description = "User's password", example = "secret123")
    @NotBlank String password
) {}