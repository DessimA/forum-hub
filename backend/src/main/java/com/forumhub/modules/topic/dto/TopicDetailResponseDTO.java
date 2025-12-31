package com.forumhub.modules.topic.dto;

import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.topic.model.TopicStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TopicDetailResponseDTO(
    UUID id,
    String title,
    String message,
    LocalDateTime createdAt,
    TopicStatus status,
    String author,
    UUID authorId,
    String authorEmail,
    String course,
    UUID courseId,
    Boolean authorIsBlocked,
    LocalDateTime authorBlockedUntil,
    List<AnswerResponseDTO> answers
) {}
