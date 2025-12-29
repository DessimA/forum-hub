package com.forumhub.modules.topic.mapper;

import com.forumhub.modules.topic.dto.TopicDetailResponseDTO;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import com.forumhub.modules.topic.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Topic toEntity(TopicRequestDTO dto);

    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorEmail", source = "author.email")
    @Mapping(target = "course", source = "course.name")
    TopicResponseDTO toResponseDTO(Topic topic);

    @Mapping(target = "author", source = "author.name")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorEmail", source = "author.email")
    @Mapping(target = "course", source = "course.name")
    @Mapping(target = "answers", ignore = true)
    TopicDetailResponseDTO toDetailDTO(Topic topic);
}