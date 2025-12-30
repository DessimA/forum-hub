package com.forumhub.modules.answer.service;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.topic.service.TopicValidationService;
import com.forumhub.security.SecurityValidationService;
import com.forumhub.modules.audit.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private static final Logger log = LoggerFactory.getLogger(AnswerService.class);
    
    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final SecurityValidationService securityService;
    private final TopicValidationService topicValidationService;
    private final AnswerMapper mapper;
    private final AuditLogService auditLogService;

    @Transactional
    public AnswerResponseDTO create(@NonNull UUID topicId, @NonNull AnswerRequestDTO answerRequest) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        topicValidationService.validateTopicOpen(topic);
        var author = securityService.getCurrentAuthenticatedUser();

        var answer = mapper.toEntity(answerRequest);
        answer.setTopic(topic);
        answer.setAuthor(author);

        return mapper.toResponseDTO(answerRepository.save(answer));
    }

    @Transactional
    public void delete(@NonNull UUID answerId, String justification) {
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
        
        securityService.validateOwnerOrModerator(answer.getAuthor());
        
        String logContent = String.format("Message: %s | Author: %s | TopicId: %s", 
                answer.getMessage(), answer.getAuthor().getEmail(), answer.getTopic().getId().toString());
        
        auditLogService.logAction("DELETE", "ANSWER", answer.getId(), logContent, justification);
        
        var topic = answer.getTopic();
        answerRepository.delete(answer);

        boolean hasRemainingSolution = answerRepository.findAllByTopicId(topic.getId()).stream().anyMatch(a -> a.getSolution());
        if (!hasRemainingSolution && topic.getStatus() == TopicStatus.RESOLVED) {
            topic.setStatus(TopicStatus.OPEN);
            topicRepository.save(topic);
        }
    }

    @Transactional
    public AnswerResponseDTO toggleSolution(@NonNull UUID answerId) {
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
        
        var topic = answer.getTopic();
        if (!securityService.isResourceOwner(topic.getAuthor().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Only topic author can mark solutions");
        }

        boolean newSolutionState = !answer.getSolution();
        answer.setSolution(newSolutionState);
        answerRepository.save(answer);

        boolean hasAnySolution = answerRepository.findAllByTopicId(topic.getId()).stream().anyMatch(a -> a.getSolution());
        topic.setStatus(hasAnySolution ? TopicStatus.RESOLVED : TopicStatus.OPEN);
        topicRepository.save(topic);

        return mapper.toResponseDTO(answer);
    }
}