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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "topics", allEntries = true)
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

    @Cacheable(value = "topics", key = "{#pageable.pageNumber, #courseId, #status, #year, #title}")
    public com.forumhub.util.RestPage<TopicResponseDTO> list(org.springframework.data.domain.Pageable pageable, UUID courseId, TopicStatus status, Integer year, String title) {
        var spec = com.forumhub.modules.topic.repository.TopicSpecification.filterBy(courseId, status, year, title);
        var page = topicRepository.findAll(spec, pageable).map(topicMapper::toResponseDTO);
        return new com.forumhub.util.RestPage<>(page.getContent(), page.getPageable(), page.getTotalElements());
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
                detail.status(), detail.author(), detail.authorId(), detail.authorEmail(), 
                detail.course(), detail.courseId(), 
                topic.getAuthor().getIsPermanentBlocked(),
                topic.getAuthor().getBlockedUntil(),
                answers
        );
    }

    @Transactional
    @CacheEvict(value = {"topics", "courses"}, allEntries = true)
    public TopicResponseDTO update(UUID id, TopicUpdateDTO dto) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        if (dto.title() != null && !dto.title().isBlank()) topic.setTitle(dto.title());
        if (dto.message() != null && !dto.message().isBlank()) topic.setMessage(dto.message());
        
        if (dto.courseId() != null) {
            var newCourse = courseRepository.findById(dto.courseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            topic.setCourse(newCourse);
        }
        
        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Transactional
    @CacheEvict(value = "topics", allEntries = true)
    public TopicResponseDTO close(UUID id) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        topic.setStatus(TopicStatus.CLOSED);
        log.info("Topic {} closed by user", id);
        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Transactional
    @CacheEvict(value = "topics", allEntries = true)
    public TopicResponseDTO reopen(UUID id) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        topic.setStatus(TopicStatus.OPEN);
        log.info("Topic {} reopened by user", id);
        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Transactional
    @CacheEvict(value = "topics", allEntries = true)
    public void delete(UUID id, String justification) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        // 1. Audit and delete answers before topic deletion
        var answers = answerRepository.findAllByTopicId(topic.getId());
        for (var answer : answers) {
            String answerLog = String.format("Message: %s | Author: %s | TopicId: %s", 
                    answer.getMessage(), answer.getAuthor().getEmail(), topic.getId().toString());
            auditLogService.logAction("DELETE", "ANSWER", answer.getId(), answerLog, "Cascade delete from topic: " + topic.getTitle());
            
            // Explicitly delete answer to clear Hibernate session state
            answerRepository.delete(answer);
        }

        // 2. Audit topic
        String logContent = String.format("Title: %s | Message: %s | Author: %s | CourseId: %s", 
                topic.getTitle(), topic.getMessage(), topic.getAuthor().getEmail(), topic.getCourse().getId().toString());
        
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), logContent, justification);
        
        // Ensure changes are flushed before final delete if necessary, 
        // but topicRepository.delete(topic) will handle it.
        topicRepository.delete(topic);
    }
}
