package com.forumhub.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicUpdateDTO(
    @NotBlank String title,
    @NotBlank String message
) {}
