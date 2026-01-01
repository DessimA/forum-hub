package com.forumhub.modules.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Schema(description = "Data for updating a topic")
public record TopicUpdateDTO(
    @Schema(description = "New topic title", example = "How to configure Spring Security (Updated)")
    String title,

    @Schema(description = "New topic message", example = "I am facing a different error now...")
    String message,

    @Schema(description = "ID of the new related course")
    UUID courseId
) {}