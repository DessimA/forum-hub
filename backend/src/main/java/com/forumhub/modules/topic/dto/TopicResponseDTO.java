package com.forumhub.modules.topic.dto;

public record TopicResponseDTO(
    Long id,
    String title,
    String message,
    java.time.LocalDateTime createdAt,
    com.forumhub.modules.topic.model.TopicStatus status,
    String author,
    Long authorId,
    String authorEmail,
    String course
) {}