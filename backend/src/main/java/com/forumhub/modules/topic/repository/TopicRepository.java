package com.forumhub.modules.topic.repository;

import com.forumhub.modules.topic.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @NonNull
    Page<Topic> findAll(@NonNull Pageable pageable);
    
    boolean existsByTitleAndMessage(String title, String message);
}