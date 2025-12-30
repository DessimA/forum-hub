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
    public void restore(UUID logId) {
        AuditLog logEntry = auditLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Log not found"));

        if (!"DELETE".equals(logEntry.getActionType())) {
            throw new BusinessValidationException("Only deleted items can be restored");
        }

        if ("TOPIC".equals(logEntry.getEntityType())) {
            restoreTopic(logEntry);
        } else if ("ANSWER".equals(logEntry.getEntityType())) {
            restoreAnswer(logEntry);
        }

        auditLogRepository.delete(logEntry);
        log.info("Entity {} ID {} restored successfully with original UUID", logEntry.getEntityType(), logEntry.getEntityId());
    }

    private void restoreTopic(AuditLog logEntry) {
        String content = logEntry.getContent();
        try {
            String title = extractValue(content, "Title: ", " | Message:");
            String message = extractValue(content, "Message: ", " | Author:");
            String authorEmail = extractValue(content, "Author: ", " | CourseId:");
            UUID courseId = UUID.fromString(content.substring(content.lastIndexOf("CourseId: ") + 10));

            var author = userRepository.findByEmail(authorEmail)
                    .orElseThrow(() -> new BusinessValidationException("Author not found: " + authorEmail));
            var course = courseRepository.findById(courseId).orElseThrow();

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
            throw new BusinessValidationException("Failed to restore topic: content corrupted or dependencies missing");
        }
    }

    private void restoreAnswer(AuditLog logEntry) {
        String content = logEntry.getContent();
        try {
            String message = extractValue(content, "Message: ", " | Author:");
            String authorEmail = extractValue(content, "Author: ", " | TopicId:");
            UUID topicId = UUID.fromString(content.substring(content.lastIndexOf("TopicId: ") + 9));

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
            throw new BusinessValidationException("Failed to restore answer: content corrupted");
        }
    }

    private String extractValue(String text, String start, String end) {
        int startIndex = text.indexOf(start) + start.length();
        int endIndex = text.indexOf(end);
        return text.substring(startIndex, endIndex);
    }

    @Transactional
    public void permanentDelete(UUID logId) {
        auditLogRepository.deleteById(logId);
    }
}