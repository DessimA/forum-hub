package com.forumhub.modules.answer.mapper;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.model.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "solution", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Answer toEntity(AnswerRequestDTO dto);

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorEmail", source = "author.email")
    AnswerResponseDTO toResponseDTO(Answer answer);
}
