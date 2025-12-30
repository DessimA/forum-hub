package com.forumhub.modules.topic.service;

import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.*;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.security.SecurityValidationService;
import com.forumhub.modules.audit.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
    private static final Logger log = LoggerFactory.getLogger(TopicService.class);

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final AnswerRepository answerRepository;
    private final SecurityValidationService securityService;
    private final TopicValidationService validationService;
    private final TopicMapper topicMapper;
    private final AnswerMapper answerMapper;
    private final AuditLogService auditLogService;

    @Transactional
    public TopicResponseDTO create(TopicRequestDTO dto) {
        var author = securityService.getCurrentAuthenticatedUser();
        
        validationService.validateNotDuplicate(dto.title(), dto.message());
        
        var course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        
        var topicEntity = topicMapper.toEntity(dto);
        topicEntity.setAuthor(author);
        topicEntity.setCourse(course);
        topicEntity.setStatus(TopicStatus.OPEN);

        var savedTopic = topicRepository.save(topicEntity);
        log.info("Topic created with ID: {}", savedTopic.getId());
        return topicMapper.toResponseDTO(savedTopic);
    }

    public Page<TopicResponseDTO> list(Pageable pageable) {
        return topicRepository.findAll(pageable).map(topicMapper::toResponseDTO);
    }

    public TopicDetailResponseDTO getDetail(UUID id) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        var answers = answerRepository.findAllByTopicId(id).stream()
                .map(answerMapper::toResponseDTO)
                .collect(Collectors.toList());
        
        var detail = topicMapper.toDetailDTO(topic);
        return new TopicDetailResponseDTO(
                detail.id(), detail.title(), detail.message(), detail.createdAt(),
                detail.status(), detail.author(), detail.authorId(), detail.authorEmail(), detail.course(), answers
        );
    }

    @Transactional
    public TopicResponseDTO update(UUID id, TopicUpdateDTO dto) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        if (dto.title() != null) topic.setTitle(dto.title());
        if (dto.message() != null) topic.setMessage(dto.message());
        
        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Transactional
    public void delete(UUID id, String justification) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        String logContent = String.format("Title: %s | Message: %s | Author: %s | CourseId: %s", 
                topic.getTitle(), topic.getMessage(), topic.getAuthor().getEmail(), topic.getCourse().getId().toString());
        
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), logContent, justification);
        
        topicRepository.delete(topic);
    }
}
