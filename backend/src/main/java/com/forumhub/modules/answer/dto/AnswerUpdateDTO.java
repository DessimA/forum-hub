package com.forumhub.modules.answer.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerUpdateDTO(
    @NotBlank String message
) {}
