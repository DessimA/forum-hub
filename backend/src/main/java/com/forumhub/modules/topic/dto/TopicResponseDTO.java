package com.forumhub.modules.topic.dto;

import com.forumhub.modules.topic.model.TopicStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Detailed data for a topic response")
public record TopicResponseDTO(
    @Schema(description = "Topic unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Topic title", example = "Error configuring Spring Security")
    String title,

    @Schema(description = "Topic message", example = "How do I configure role-based authorization?")
    String message,

    @Schema(description = "Topic creation date")
    LocalDateTime createdAt,

    @Schema(description = "Current topic status", example = "OPEN")
    TopicStatus status,

    @Schema(description = "Author's name", example = "John Doe")
    String author,

    @Schema(description = "Author's ID", example = "123e4567-e89b-12d3-a456-426614174001")
    UUID authorId,

    @Schema(description = "Author's email", example = "john.doe@example.com")
    String authorEmail,

    @Schema(description = "Related course name", example = "Spring Boot")
    String course,

    @Schema(description = "Related course ID", example = "123e4567-e89b-12d3-a456-426614174002")
    UUID courseId
) {}
