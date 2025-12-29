package com.forumhub.modules.topic.dto;

import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.topic.model.TopicStatus;
import java.time.LocalDateTime;
import java.util.List;

public record TopicDetailResponseDTO(
    Long id,
    String title,
    String message,
    LocalDateTime createdAt,
    TopicStatus status,
    String author,
    Long authorId,
    String authorEmail,
    String course,
    List<AnswerResponseDTO> answers
) {}