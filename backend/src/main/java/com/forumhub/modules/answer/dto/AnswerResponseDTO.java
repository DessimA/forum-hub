package com.forumhub.modules.answer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response data for an answer")
public record AnswerResponseDTO(
    @Schema(description = "Answer unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Answer content", example = "This is a solution to the problem...")
    String message,

    @Schema(description = "Related topic ID")
    UUID topicId,

    @Schema(description = "Answer creation date")
    LocalDateTime createdAt,

    @Schema(description = "Answer author's name", example = "Jane Smith")
    String author,

    @Schema(description = "Answer author's ID")
    UUID authorId,

    @Schema(description = "Answer author's email", example = "jane.smith@example.com")
    String authorEmail,

    @Schema(description = "Indicates if this answer was marked as a solution", example = "true")
    Boolean solution
) {}
