package com.forumhub.modules.favorite.service;

import com.forumhub.modules.favorite.entity.UserFavorite;
import com.forumhub.modules.favorite.repository.UserFavoriteRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.security.SecurityValidationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private UserFavoriteRepository favoriteRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SecurityValidationService securityService;

    @Test
    @DisplayName("Should throw EntityNotFoundException when topic does not exist")
    void addFavorite_TopicNotFound() {
        UUID topicId = UUID.randomUUID();
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(new User());
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> favoriteService.addFavorite(topicId));
    }

    @Test
    @DisplayName("Should add favorite successfully")
    void addFavorite_Success() {
        UUID topicId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Topic topic = new Topic();
        topic.setId(topicId);

        when(securityService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(favoriteRepository.existsByUserIdAndTopicId(user.getId(), topicId)).thenReturn(false);

        favoriteService.addFavorite(topicId);

        verify(favoriteRepository, times(1)).save(any(UserFavorite.class));
    }
}
