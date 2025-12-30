package com.forumhub.modules.answer.service;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.mapper.AnswerMapper;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.audit.service.AuditLogService;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.topic.service.TopicValidationService;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock private AnswerRepository answerRepository;
    @Mock private TopicRepository topicRepository;
    @Mock private SecurityValidationService securityService;
    @Mock private TopicValidationService topicValidationService;
    @Mock private AnswerMapper answerMapper;
    @Mock private AuditLogService auditLogService;

    private User currentUser;
    private User otherUser;
    private Topic topic;
    private Answer answer;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        Profile userProfile = new Profile(UUID.randomUUID(), "ROLE_USER");
        currentUser = User.builder().id(userId).email("user@example.com").profile(userProfile).build();
        
        UUID otherId = UUID.randomUUID();
        Profile otherProfile = new Profile(UUID.randomUUID(), "ROLE_USER");
        otherUser = User.builder().id(otherId).email("other@example.com").profile(otherProfile).build();

        UUID topicId = UUID.randomUUID();
        topic = Topic.builder().id(topicId).title("Test Topic").author(otherUser).status(TopicStatus.OPEN).build();
        answer = Answer.builder().id(UUID.randomUUID()).message("Test Answer").topic(topic).author(currentUser).solution(false).build();
    }

    @Test
    @DisplayName("Should create answer when data is valid")
    void create_WithValidData_ShouldCreateAnswer() {
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(currentUser);
        AnswerRequestDTO requestDTO = new AnswerRequestDTO("Test Answer");
        
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(answerMapper.toEntity(requestDTO)).thenReturn(answer);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        
        AnswerResponseDTO expectedResponse = new AnswerResponseDTO(answer.getId(), "Test Answer", topic.getId(), LocalDateTime.now(), "User Name", currentUser.getId(), "user@example.com", false);
        when(answerMapper.toResponseDTO(answer)).thenReturn(expectedResponse);

        AnswerResponseDTO result = answerService.create(topic.getId(), requestDTO);

        assertThat(result).isNotNull();
        verify(answerRepository).save(any(Answer.class));
        verify(topicValidationService).validateTopicOpen(topic);
    }

    @Test
    @DisplayName("Should toggle solution status and update topic to RESOLVED")
    void toggleSolution_AsTopicAuthor_ShouldUpdateTopicStatus() {
        topic.setAuthor(currentUser); 
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(securityService.isResourceOwner(currentUser.getId())).thenReturn(true);
        when(answerRepository.findAllByTopicId(topic.getId())).thenReturn(List.of(answer));
        
        answerService.toggleSolution(answer.getId());

        assertThat(answer.getSolution()).isTrue();
        assertThat(topic.getStatus()).isEqualTo(TopicStatus.RESOLVED);
        verify(topicRepository).save(topic);
    }

    @Test
    @DisplayName("Should update topic to OPEN when last solution is removed")
    void toggleSolution_RemoveLastSolution_ShouldSetTopicOpen() {
        topic.setAuthor(currentUser);
        topic.setStatus(TopicStatus.RESOLVED);
        answer.setSolution(true);
        
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(securityService.isResourceOwner(currentUser.getId())).thenReturn(true);
        when(answerRepository.findAllByTopicId(topic.getId())).thenReturn(List.of(answer));
        
        answerService.toggleSolution(answer.getId());

        assertThat(answer.getSolution()).isFalse();
        assertThat(topic.getStatus()).isEqualTo(TopicStatus.OPEN);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not topic author during toggle")
    void toggleSolution_AsNonAuthor_ShouldThrowException() {
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(securityService.isResourceOwner(otherUser.getId())).thenReturn(false);

        assertThatThrownBy(() -> answerService.toggleSolution(answer.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Should delete answer when validation passes")
    void delete_ValidationPasses_ShouldDelete() {
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(answerRepository.findAllByTopicId(topic.getId())).thenReturn(List.of());
        
        answerService.delete(answer.getId(), "Just because");

        verify(securityService).validateOwnerOrModerator(answer.getAuthor());
        verify(answerRepository).delete(answer);
        verify(auditLogService).logAction(eq("DELETE"), eq("ANSWER"), eq(answer.getId()), any(), eq("Just because"));
    }
}
