package com.forumhub.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Data for updating a user")
public record UserUpdateDTO(
    @Schema(description = "New user name", example = "John Smith")
    @NotBlank String name,

    @Schema(description = "New user email", example = "john.smith@example.com")
    @NotBlank @Email String email,

    @Schema(description = "New password (optional)", example = "newPassword123")
    @Size(min = 6) String password
) {}