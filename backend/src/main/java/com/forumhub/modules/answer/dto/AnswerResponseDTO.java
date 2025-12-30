package com.forumhub.modules.answer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnswerResponseDTO(
    UUID id,
    String message,
    UUID topicId,
    LocalDateTime createdAt,
    String author,
    UUID authorId,
    String authorEmail,
    Boolean solution
) {}