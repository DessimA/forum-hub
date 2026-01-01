package com.forumhub.modules.answer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data for creating an answer")
public record AnswerRequestDTO(
    @Schema(description = "Answer content", example = "To solve this, you should add the @Service annotation...")
    @NotBlank String message
) {}