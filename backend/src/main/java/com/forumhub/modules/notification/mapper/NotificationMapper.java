package com.forumhub.modules.notification.mapper;

import com.forumhub.modules.notification.dto.NotificationResponseDTO;
import com.forumhub.modules.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "topicTitle", source = "topic.title")
    @Mapping(target = "notificationType", source = "type")
    NotificationResponseDTO toResponseDTO(Notification notification);
}
