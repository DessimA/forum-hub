package com.forumhub.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for registering a new user")
public record RegisterRequestDTO(
    @Schema(description = "User's full name", example = "John Doe")
    @NotBlank String name,

    @Schema(description = "User's email for login", example = "john.doe@example.com")
    @NotBlank @Email String email,

    @Schema(description = "User's password (minimum 6 characters)", example = "secret123")
    @NotBlank @Size(min = 6) String password
) {}