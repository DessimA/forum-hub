package com.forumhub.modules.notification.service;

import com.forumhub.modules.notification.dto.NotificationResponseDTO;
import com.forumhub.modules.notification.dto.UnreadCountResponseDTO;
import com.forumhub.modules.notification.entity.Notification;
import com.forumhub.modules.notification.entity.NotificationType;
import com.forumhub.modules.notification.mapper.NotificationMapper;
import com.forumhub.modules.notification.repository.NotificationRepository;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.security.SecurityValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SecurityValidationService securityService;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final NotificationMapper mapper;

    @Transactional
    public void createNotification(UUID userId, UUID topicId, NotificationType type, String content) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        var topic = topicId != null ? topicRepository.findById(topicId).orElse(null) : null;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTopic(topic);
        notification.setType(type);
        notification.setContent(content);
        
        notificationRepository.save(notification);
    }

    public List<NotificationResponseDTO> getUserNotifications() {
        var currentUser = securityService.getCurrentAuthenticatedUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UnreadCountResponseDTO getUnreadCount() {
        var currentUser = securityService.getCurrentAuthenticatedUser();
        long count = notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
        return new UnreadCountResponseDTO(count);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        
        securityService.validateOwner(notification.getUser());
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        var currentUser = securityService.getCurrentAuthenticatedUser();
        var notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        
        notifications.forEach(n -> {
            if (!n.getIsRead()) {
                n.setIsRead(true);
            }
        });
        
        notificationRepository.saveAll(notifications);
    }
}
