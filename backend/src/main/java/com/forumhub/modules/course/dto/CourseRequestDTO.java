package com.forumhub.modules.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDTO(
    @NotBlank String name,
    @NotBlank String category
) {}
