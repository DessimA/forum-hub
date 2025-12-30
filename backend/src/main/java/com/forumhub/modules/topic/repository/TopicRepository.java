package com.forumhub.modules.topic.repository;

import com.forumhub.modules.topic.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    boolean existsByTitleAndMessage(String title, String message);
}
