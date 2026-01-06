package com.forumhub.modules.audit.service;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.exception.ResourceNotFoundException;
import com.forumhub.exception.BusinessValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final TopicRepository topicRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void logAction(String actionType, String entityType, UUID entityId, String content, String justification) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String performer = (auth != null) ? auth.getName() : "SYSTEM";
        
        var auditLog = AuditLog.builder()
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId.toString())
                .content(content)
                .justification(justification)
                .performedBy(performer)
                .build();
        
        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> listAll() {
        return auditLogRepository.findAll();
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "topics", allEntries = true)
    public void restore(UUID logId, boolean includeAnswers) {
        AuditLog logEntry = auditLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Log not found"));

        if (!"DELETE".equals(logEntry.getActionType())) {
            throw new BusinessValidationException("Only deleted items can be restored");
        }

        if ("TOPIC".equals(logEntry.getEntityType())) {
            restoreTopic(logEntry);
            
            if (includeAnswers) {
                // Find all answer logs related to this topic ID
                String topicIdMarker = "TopicId: " + logEntry.getEntityId();
                List<AuditLog> answerLogs = auditLogRepository.findAllByActionTypeAndEntityTypeAndContentContaining(
                        "DELETE", "ANSWER", topicIdMarker);
                
                for (AuditLog answerLog : answerLogs) {
                    try {
                        restoreAnswer(answerLog);
                        auditLogRepository.delete(answerLog);
                    } catch (Exception e) {
                        log.warn("Failed to restore cascaded answer {}: {}", answerLog.getEntityId(), e.getMessage());
                    }
                }
            }
        } else if ("ANSWER".equals(logEntry.getEntityType())) {
            restoreAnswer(logEntry);
        }

        auditLogRepository.delete(logEntry);
        log.info("Entity {} ID {} restored successfully (IncludeAnswers: {})", 
                logEntry.getEntityType(), logEntry.getEntityId(), includeAnswers);
    }

    private void restoreTopic(AuditLog logEntry) {
        String content = logEntry.getContent();
        try {
            // Robust parsing: find the markers and extract content between them
            // Title: [Value] | Message: [Value] | Author: [Value] | CourseId: [Value]
            String title = extractRobust(content, "Title: ", " | Message: ");
            String message = extractRobust(content, " | Message: ", " | Author: ");
            String authorEmail = extractRobust(content, " | Author: ", " | CourseId: ");
            String courseIdStr = content.substring(content.lastIndexOf("CourseId: ") + 10).trim();
            UUID courseId = UUID.fromString(courseIdStr);

            var author = userRepository.findByEmail(authorEmail)
                    .orElseThrow(() -> new BusinessValidationException("Author not found: " + authorEmail));
            var course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new BusinessValidationException("Original course (ID: " + courseId + ") no longer exists."));

            Topic topic = Topic.builder()
                    .id(UUID.fromString(logEntry.getEntityId()))
                    .title(title)
                    .message(message)
                    .author(author)
                    .course(course)
                    .status(TopicStatus.OPEN)
                    .build();
            topicRepository.save(topic);
        } catch (BusinessValidationException be) {
            throw be;
        } catch (Exception e) {
            log.error("Restoration failed for log {}: {}", logEntry.getId(), e.getMessage());
            throw new BusinessValidationException("Failed to restore topic: data corrupted or dependencies missing.");
        }
    }

    private void restoreAnswer(AuditLog logEntry) {
        String content = logEntry.getContent();
        try {
            // Message: [Value] | Author: [Value] | TopicId: [Value]
            String message = extractRobust(content, "Message: ", " | Author: ");
            String authorEmail = extractRobust(content, " | Author: ", " | TopicId: ");
            String topicIdStr = content.substring(content.lastIndexOf("TopicId: ") + 9).trim();
            UUID topicId = UUID.fromString(topicIdStr);

            var author = userRepository.findByEmail(authorEmail)
                    .orElseThrow(() -> new BusinessValidationException("Author not found: " + authorEmail));
            var topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new BusinessValidationException("Original topic (UUID: "+topicId+") no longer exists. Restore the topic first."));

            Answer answer = Answer.builder()
                    .id(UUID.fromString(logEntry.getEntityId()))
                    .message(message)
                    .author(author)
                    .topic(topic)
                    .build();
            answerRepository.save(answer);
        } catch (BusinessValidationException be) {
            throw be;
        } catch (Exception e) {
            log.error("Restoration failed for log {}: {}", logEntry.getId(), e.getMessage());
            throw new BusinessValidationException("Failed to restore answer: data corrupted.");
        }
    }

    private String extractRobust(String text, String startMarker, String endMarker) {
        int start = text.indexOf(startMarker);
        if (start == -1) return "";
        start += startMarker.length();
        
        int end = text.indexOf(endMarker, start);
        if (end == -1) return text.substring(start);
        
        return text.substring(start, end);
    }

    private String extractValue(String text, String start, String end) {
        return extractRobust(text, start, end);
    }

    @Transactional
    public void permanentDelete(UUID logId) {
        auditLogRepository.deleteById(logId);
    }
}