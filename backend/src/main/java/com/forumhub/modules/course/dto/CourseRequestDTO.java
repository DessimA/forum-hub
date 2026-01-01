package com.forumhub.modules.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data for creating a new course")
public record CourseRequestDTO(
    @Schema(description = "Course name", example = "Spring Boot 3")
    @NotBlank String name,

    @Schema(description = "Course category", example = "Programming")
    @NotBlank String category
) {}