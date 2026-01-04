package com.forumhub.modules.topic.service;

import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.TopicDetailResponseDTO;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.security.SecurityValidationService;
import com.forumhub.modules.audit.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    private TopicService service;
    @Mock private TopicRepository topicRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private SecurityValidationService securityService;
    @Mock private TopicValidationService validationService;
    @Mock private TopicMapper topicMapper;
    @Mock private AnswerMapper answerMapper;
    @Mock private AuditLogService auditLogService;
    @Mock private com.forumhub.modules.favorite.repository.UserFavoriteRepository favoriteRepository;
    @Mock private com.forumhub.modules.notification.service.NotificationService notificationService;

    private User user;
    private Course course;
    private Topic topic;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).email("author@test.com").build();
        course = new Course(UUID.randomUUID(), "Course", "Cat", null);
        topic = Topic.builder().id(UUID.randomUUID()).title("Title").message("Msg").author(user).course(course).build();
        
        service = new TopicService(topicRepository, courseRepository, answerRepository, securityService, validationService, topicMapper, answerMapper, auditLogService, favoriteRepository, notificationService);
    }

    @Test
    @DisplayName("Should create topic successfully")
    void shouldCreateTopic() {
        var dto = new TopicRequestDTO("Title", "Msg", course.getId());
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(topicMapper.toEntity(dto)).thenReturn(topic);
        when(topicRepository.save(any())).thenReturn(topic);
        when(topicMapper.toResponseDTO(topic)).thenReturn(mock(TopicResponseDTO.class));

        service.create(dto);

        verify(topicRepository).save(any());
        verify(validationService).validateNotDuplicate(anyString(), anyString());
    }

    @Test
    @DisplayName("Should get topic detail")
    void shouldGetDetail() {
        UUID id = topic.getId();
        when(topicRepository.findById(id)).thenReturn(Optional.of(topic));
        when(answerRepository.findAllByTopicId(id)).thenReturn(Collections.emptyList());
        when(topicMapper.toDetailDTO(topic)).thenReturn(new TopicDetailResponseDTO(id, "T", "M", null, TopicStatus.OPEN, "A", user.getId(), "e", "C", course.getId(), false, null, Collections.emptyList()));

        var result = service.getDetail(id);

        assertThat(result).isNotNull();
        verify(answerRepository).findAllByTopicId(id);
    }

    @Test
    @DisplayName("Should delete topic and log action")
    void shouldDeleteTopic() {
        UUID id = topic.getId();
        when(topicRepository.findById(id)).thenReturn(Optional.of(topic));
        when(favoriteRepository.findAllByTopicId(id)).thenReturn(java.util.Collections.emptyList());

        service.delete(id, "Justification");

        verify(topicRepository).delete(topic);
        verify(favoriteRepository).deleteAllByTopicId(id);
        verify(answerRepository).deleteAllByTopicId(id);
        verify(auditLogService).logAction(eq("DELETE"), eq("TOPIC"), eq(id), anyString(), eq("Justification"));
    }
}
