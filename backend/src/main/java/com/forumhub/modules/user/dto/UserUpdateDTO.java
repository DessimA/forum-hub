package com.forumhub.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
    @NotBlank String name,
    @NotBlank @Email String email,
    @Size(min = 6) String password
) {}
