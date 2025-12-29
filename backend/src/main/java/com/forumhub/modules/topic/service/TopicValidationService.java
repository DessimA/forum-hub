package com.forumhub.modules.topic.service;

import com.forumhub.exception.BusinessValidationException;
import com.forumhub.exception.DuplicateResourceException;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicValidationService {

    private final TopicRepository repository;

    public void validateNotDuplicate(String title, String message) {
        if (repository.existsByTitleAndMessage(title, message)) {
            throw new DuplicateResourceException("A topic with the same title and message already exists");
        }
    }

    public void validateTopicStatus(Topic topic, TopicStatus requiredStatus) {
        if (topic.getStatus() != requiredStatus) {
            throw new BusinessValidationException("Topic is not in the required status: " + requiredStatus);
        }
    }

    public void validateTopicOpen(Topic topic) {
        if (topic.getStatus() == TopicStatus.CLOSED) {
            throw new BusinessValidationException("Topic is closed for interactions");
        }
    }
}
