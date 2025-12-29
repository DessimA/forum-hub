package com.forumhub.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopicRequestDTO(
    @NotBlank String title,
    @NotBlank String message,
    @NotNull Long courseId
) {}
