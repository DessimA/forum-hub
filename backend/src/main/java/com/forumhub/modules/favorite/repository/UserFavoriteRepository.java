package com.forumhub.modules.favorite.repository;

import com.forumhub.modules.favorite.entity.UserFavorite;
import com.forumhub.modules.favorite.entity.UserFavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UserFavoriteId> {
    List<UserFavorite> findAllByUserId(UUID userId);
    Page<UserFavorite> findAllByUserId(UUID userId, Pageable pageable);
    List<UserFavorite> findAllByTopicId(UUID topicId);
    
    @Modifying
    @Transactional
    void deleteAllByTopicId(UUID topicId);
    
    boolean existsByUserIdAndTopicId(UUID userId, UUID topicId);
    void deleteByUserIdAndTopicId(UUID userId, UUID topicId);
}
