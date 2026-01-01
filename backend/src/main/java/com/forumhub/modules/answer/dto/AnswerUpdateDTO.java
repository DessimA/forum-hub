package com.forumhub.modules.answer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data for updating an answer")
public record AnswerUpdateDTO(
    @Schema(description = "New answer content", example = "Correction: The correct annotation is @ControllerAdvice...")
    @NotBlank String message
) {}