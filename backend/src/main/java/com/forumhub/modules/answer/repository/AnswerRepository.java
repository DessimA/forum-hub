package com.forumhub.modules.answer.repository;

import com.forumhub.modules.answer.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByTopicId(Long topicId);
}
