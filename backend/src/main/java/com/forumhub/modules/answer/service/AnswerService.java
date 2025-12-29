package com.forumhub.modules.answer.service;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.security.SecurityValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final SecurityValidationService securityService;
    private final AnswerMapper answerMapper;
    private final com.forumhub.modules.audit.service.AuditLogService auditLogService;

    @Transactional
    public AnswerResponseDTO reply(@NonNull Long topicId, @NonNull AnswerRequestDTO answerRequest) {
        log.info("Adding new answer to topic ID: {}", topicId);
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));
        
        var author = securityService.getCurrentAuthenticatedUser();

        var answerEntity = answerMapper.toEntity(answerRequest);
        answerEntity.setTopic(topic);
        answerEntity.setAuthor(author);

        var savedAnswer = answerRepository.save(answerEntity);
        log.info("Answer added successfully with ID: {} to topic ID: {}", savedAnswer.getId(), topicId);
        
        return answerMapper.toResponseDTO(savedAnswer);
    }

    @Transactional
    public AnswerResponseDTO markAsSolution(@NonNull Long answerId) {
        log.info("Toggling solution status for answer ID: {}", answerId);
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with ID: " + answerId));
        
        var currentUser = securityService.getCurrentAuthenticatedUser();

        if (!answer.getTopic().getAuthor().getId().equals(currentUser.getId())) {
            log.warn("User ID: {} attempted to mark solution on topic ID: {} without permission", currentUser.getId(), answer.getTopic().getId());
            throw new org.springframework.security.access.AccessDeniedException("Only the topic author can mark a solution");
        }

        answer.setSolution(!answer.getSolution());
        log.info("Answer ID: {} solution status set to: {}", answerId, answer.getSolution());
        
        var topic = answer.getTopic();
        boolean hasOtherSolution = answerRepository.findAllByTopicId(topic.getId())
                .stream().anyMatch(r -> r.getSolution() && !r.getId().equals(answer.getId()));

        var newStatus = (answer.getSolution() || hasOtherSolution) ? TopicStatus.RESOLVED : TopicStatus.OPEN;
        log.debug("Updating topic ID: {} status to: {}", topic.getId(), newStatus);
        topic.setStatus(newStatus);

        return answerMapper.toResponseDTO(answer);
    }

    @Transactional
    public void delete(@NonNull Long answerId, String justification) {
        log.info("Deleting answer ID: {} with justification: {}", answerId, justification);
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with ID: " + answerId));
        
        securityService.validateOwnerOrModerator(answer.getAuthor());
        
        auditLogService.logAction("DELETE", "ANSWER", answer.getId(), answer.getMessage(), justification);
        answerRepository.delete(answer);
        log.info("Answer ID: {} deleted successfully", answerId);
    }
}
