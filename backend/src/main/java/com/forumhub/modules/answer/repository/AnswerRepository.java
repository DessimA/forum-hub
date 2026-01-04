package com.forumhub.modules.answer.repository;

import com.forumhub.modules.answer.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findAllByTopicId(UUID topicId);
    
    @Modifying
    @Transactional
    void deleteAllByTopicId(UUID topicId);
}