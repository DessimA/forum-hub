package com.forumhub.modules.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "Data for creating a new topic")
public record TopicRequestDTO(
    @Schema(description = "Topic title", example = "How to solve error X in Spring Boot?")
    @NotBlank String title,

    @Schema(description = "Topic message or content", example = "I am facing a NullPointerException when...")
    @NotBlank String message,

    @Schema(description = "ID of the related course", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull UUID courseId
) {}
