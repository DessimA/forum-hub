package com.forumhub.modules.favorite.service;

import com.forumhub.exception.DuplicateResourceException;
import com.forumhub.modules.favorite.entity.UserFavorite;
import com.forumhub.modules.favorite.repository.UserFavoriteRepository;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.security.SecurityValidationService;
import com.forumhub.util.RestPage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserFavoriteRepository favoriteRepository;
    private final TopicRepository topicRepository;
    private final SecurityValidationService securityService;
    private final TopicMapper topicMapper;

    @Transactional
    public void addFavorite(UUID topicId) {
        var user = securityService.getCurrentAuthenticatedUser();
        
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        if (favoriteRepository.existsByUserIdAndTopicId(user.getId(), topicId)) {
            throw new DuplicateResourceException("Topic already favorited");
        }

        UserFavorite favorite = new UserFavorite(user, topic);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(UUID topicId) {
        var user = securityService.getCurrentAuthenticatedUser();
        
        if (!topicRepository.existsById(topicId)) {
            throw new EntityNotFoundException("Topic not found");
        }

        favoriteRepository.deleteByUserIdAndTopicId(user.getId(), topicId);
    }

    public Page<TopicResponseDTO> listUserFavorites(Pageable pageable) {
        var user = securityService.getCurrentAuthenticatedUser();
        var page = favoriteRepository.findAllByUserId(user.getId(), pageable)
                .map(UserFavorite::getTopic)
                .map(topicMapper::toResponseDTO);
        return new RestPage<>(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    // Keep this for simple list retrieval if needed internally, or just use pageable
    public List<TopicResponseDTO> listUserFavoritesAll() {
        var user = securityService.getCurrentAuthenticatedUser();
        return favoriteRepository.findAllByUserId(user.getId()).stream()
                .map(UserFavorite::getTopic)
                .map(topicMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public boolean isFavorited(UUID topicId) {
        var user = securityService.getCurrentAuthenticatedUser();
        return favoriteRepository.existsByUserIdAndTopicId(user.getId(), topicId);
    }
}