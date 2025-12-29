package com.forumhub.modules.answer.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequestDTO(
    @NotBlank String message
) {}
