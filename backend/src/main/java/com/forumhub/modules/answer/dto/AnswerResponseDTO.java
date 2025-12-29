package com.forumhub.modules.answer.dto;

public record AnswerResponseDTO(
    Long id,
    String message,
    Long topicId,
    java.time.LocalDateTime createdAt,
    String author,
    Long authorId,
    String authorEmail,
    Boolean solution
) {}
