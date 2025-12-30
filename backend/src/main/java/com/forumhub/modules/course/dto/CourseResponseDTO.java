package com.forumhub.modules.course.dto;

import java.util.UUID;

public record CourseResponseDTO(
    UUID id,
    String name,
    String category
) {}