package com.forumhub.modules.topic.service;

import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.security.SecurityValidationService;
import com.forumhub.modules.audit.service.AuditLogService;
import com.forumhub.exception.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceUniquenessTest {

    @Mock private TopicRepository topicRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private SecurityValidationService securityService;
    @Mock private TopicValidationService validationService;
    @Mock private TopicMapper topicMapper;
    @Mock private AnswerMapper answerMapper;
    @Mock private AuditLogService auditLogService;
    
    private TopicService service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new TopicService(
            topicRepository,
            courseRepository,
            answerRepository,
            securityService,
            validationService,
            topicMapper,
            answerMapper,
            auditLogService
        );
    }

    @Test
    @DisplayName("Should throw exception when duplicate title and message exists")
    void shouldThrowOnDuplicate() {
        var dto = new TopicRequestDTO("Title", "Message", UUID.randomUUID());
        
        // Ensure security check doesn't crash even if we don't care about it here
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(mock(com.forumhub.modules.user.model.User.class));

        // Mock the validation service which checks for duplicates
        doThrow(new DuplicateResourceException("Tópico duplicado"))
            .when(validationService).validateNotDuplicate("Title", "Message");

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }
}