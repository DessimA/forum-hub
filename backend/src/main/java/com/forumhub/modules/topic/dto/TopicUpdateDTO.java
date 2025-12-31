package com.forumhub.modules.topic.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicUpdateDTO(
    String title,
    String message,
    java.util.UUID courseId
) {}
