package com.forumhub.modules.topic.service;

import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.*;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.security.SecurityValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;
    private final AnswerRepository answerRepository;
    private final SecurityValidationService securityService;
    private final TopicValidationService validationService;
    private final TopicMapper topicMapper;
    private final AnswerMapper answerMapper;
    private final com.forumhub.modules.audit.service.AuditLogService auditLogService;

    @Transactional
    public TopicResponseDTO create(@NonNull TopicRequestDTO topicRequest) {
        log.info("Creating new topic with title: {}", topicRequest.title());
        validationService.validateNotDuplicate(topicRequest.title(), topicRequest.message());
        
        var course = courseRepository.findById(topicRequest.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + topicRequest.courseId()));
        
        var author = securityService.getCurrentAuthenticatedUser();

        var topicEntity = topicMapper.toEntity(topicRequest);
        topicEntity.setAuthor(author);
        topicEntity.setCourse(course);
        topicEntity.setStatus(TopicStatus.OPEN);

        var savedTopic = topicRepository.save(topicEntity);
        log.info("Topic created successfully with ID: {}", savedTopic.getId());
        
        return topicMapper.toResponseDTO(savedTopic);
    }

    public Page<TopicResponseDTO> list(@NonNull Pageable pageable) {
        log.debug("Listing topics with pagination: {}", pageable);
        return topicRepository.findAll(pageable).map(topicMapper::toResponseDTO);
    }

    public TopicDetailResponseDTO getDetail(@NonNull Long topicId) {
        log.debug("Fetching details for topic ID: {}", topicId);
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));
        
        var answers = answerRepository.findAllByTopicId(topicId).stream()
                .map(answerMapper::toResponseDTO).toList();
        
        var detailDTO = topicMapper.toDetailDTO(topic);
        return new TopicDetailResponseDTO(
            detailDTO.id(), detailDTO.title(), detailDTO.message(), detailDTO.createdAt(), 
            detailDTO.status(), detailDTO.author(), topic.getAuthor().getId(), detailDTO.authorEmail(), detailDTO.course(), answers
        );
    }

    @Transactional
    public TopicResponseDTO update(@NonNull Long topicId, @NonNull TopicUpdateDTO updateRequest) {
        log.info("Updating topic ID: {}", topicId);
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        if (updateRequest.title() != null) {
            log.debug("Updating title for topic ID: {}", topicId);
            topic.setTitle(updateRequest.title());
        }
        if (updateRequest.message() != null) {
            log.debug("Updating message for topic ID: {}", topicId);
            topic.setMessage(updateRequest.message());
        }
        
        return topicMapper.toResponseDTO(topic);
    }

    @Transactional
    public void delete(@NonNull Long topicId, String justification) {
        log.info("Deleting topic ID: {} with justification: {}", topicId, justification);
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found with ID: " + topicId));
        
        securityService.validateOwnerOrModerator(topic.getAuthor());
        
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), topic.getTitle(), justification);
        topicRepository.delete(topic);
        log.info("Topic ID: {} deleted successfully", topicId);
    }
}