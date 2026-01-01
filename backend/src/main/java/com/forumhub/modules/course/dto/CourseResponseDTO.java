package com.forumhub.modules.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Course response data")
public record CourseResponseDTO(
    @Schema(description = "Course unique ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Course name", example = "Spring Boot 3")
    String name,

    @Schema(description = "Course category", example = "Programming")
    String category
) {}
