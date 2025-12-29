package com.forumhub.modules.topic.service;

import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.audit.service.AuditLogService;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.*;
import com.forumhub.modules.topic.mapper.TopicMapper;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.security.SecurityValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @InjectMocks
    private TopicService topicService;

    @Mock private TopicRepository topicRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private AnswerRepository answerRepository;
    @Mock private SecurityValidationService securityService;
    @Mock private TopicValidationService validationService;
    @Mock private TopicMapper topicMapper;
    @Mock private AnswerMapper answerMapper;
    @Mock private AuditLogService auditLogService;

    private User currentUser;
    private Course course;
    private Topic topic;

    @BeforeEach
    void setUp() {
        Profile userProfile = new Profile(1L, "ROLE_USER");
        currentUser = User.builder().id(1L).email("user@example.com").profile(userProfile).build();
        course = new Course(1L, "Course 1", "Category", null);
        topic = Topic.builder().id(100L).title("Title").message("Message").author(currentUser).course(course).status(TopicStatus.OPEN).build();
    }

    @Test
    @DisplayName("Should create topic when validation passes")
    void create_ValidationPasses_ShouldCreate() {
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(currentUser);
        TopicRequestDTO request = new TopicRequestDTO("Title", "Message", 1L);
        
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(topicMapper.toEntity(request)).thenReturn(topic);
        when(topicRepository.save(any(Topic.class))).thenReturn(topic);
        
        TopicResponseDTO expected = new TopicResponseDTO(100L, "Title", "Message", LocalDateTime.now(), TopicStatus.OPEN, "User", 1L, "user@example.com", "Course 1");
        when(topicMapper.toResponseDTO(topic)).thenReturn(expected);

        TopicResponseDTO result = topicService.create(request);

        assertThat(result).isNotNull();
        verify(validationService).validateNotDuplicate("Title", "Message");
        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    @DisplayName("Should update topic when validation passes")
    void update_ValidationPasses_ShouldUpdate() {
        TopicUpdateDTO update = new TopicUpdateDTO("New Title", "New Message");
        
        when(topicRepository.findById(100L)).thenReturn(Optional.of(topic));
        when(topicMapper.toResponseDTO(topic)).thenReturn(mock(TopicResponseDTO.class));

        topicService.update(100L, update);

        assertThat(topic.getTitle()).isEqualTo("New Title");
        verify(securityService).validateOwnerOrModerator(topic.getAuthor());
    }

    @Test
    @DisplayName("Should delete topic when validation passes")
    void delete_ValidationPasses_ShouldDeleteAndLog() {
        when(topicRepository.findById(100L)).thenReturn(Optional.of(topic));

        topicService.delete(100L, "Spam");

        verify(securityService).validateOwnerOrModerator(topic.getAuthor());
        verify(topicRepository).delete(topic);
        verify(auditLogService).logAction(eq("DELETE"), eq("TOPIC"), eq(100L), any(), eq("Spam"));
    }
}
