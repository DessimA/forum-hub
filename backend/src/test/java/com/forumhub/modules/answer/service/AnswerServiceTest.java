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
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.security.SecurityValidationService;
import jakarta.persistence.EntityNotFoundException;
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
    @Mock private AnswerMapper answerMapper;
    @Mock private AuditLogService auditLogService;

    private User currentUser;
    private User otherUser;
    private Topic topic;
    private Answer answer;

    @BeforeEach
    void setUp() {
        Profile userProfile = new Profile(1L, "ROLE_USER");
        currentUser = User.builder().id(1L).email("user@example.com").profile(userProfile).build();
        
        Profile otherProfile = new Profile(2L, "ROLE_USER");
        otherUser = User.builder().id(2L).email("other@example.com").profile(otherProfile).build();

        topic = Topic.builder().id(100L).title("Test Topic").author(otherUser).status(TopicStatus.OPEN).build();
        answer = Answer.builder().id(1L).message("Test Answer").topic(topic).author(currentUser).solution(false).build();
    }

    @Test
    @DisplayName("Should create answer when data is valid")
    void reply_WithValidData_ShouldCreateAnswer() {
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(currentUser);
        AnswerRequestDTO requestDTO = new AnswerRequestDTO("Test Answer");
        
        when(topicRepository.findById(100L)).thenReturn(Optional.of(topic));
        when(answerMapper.toEntity(requestDTO)).thenReturn(answer);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        
        AnswerResponseDTO expectedResponse = new AnswerResponseDTO(1L, "Test Answer", 100L, LocalDateTime.now(), "User Name", 1L, "user@example.com", false);
        when(answerMapper.toResponseDTO(answer)).thenReturn(expectedResponse);

        AnswerResponseDTO result = answerService.reply(100L, requestDTO);

        assertThat(result).isNotNull();
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    @DisplayName("Should mark as solution when user is topic author")
    void markAsSolution_AsTopicAuthor_ShouldMarkAndUpdateTopic() {
        topic.setAuthor(currentUser); 
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(currentUser);
        
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.findAllByTopicId(100L)).thenReturn(List.of(answer));
        
        when(answerMapper.toResponseDTO(any(Answer.class))).thenReturn(mock(AnswerResponseDTO.class));

        answerService.markAsSolution(1L);

        assertThat(answer.getSolution()).isTrue();
        assertThat(topic.getStatus()).isEqualTo(TopicStatus.RESOLVED);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not topic author")
    void markAsSolution_AsNonAuthor_ShouldThrowException() {
        when(securityService.getCurrentAuthenticatedUser()).thenReturn(currentUser); // Not author of topic (topic author is otherUser)
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        assertThatThrownBy(() -> answerService.markAsSolution(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Should delete answer when validation passes")
    void delete_ValidationPasses_ShouldDelete() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        
        answerService.delete(1L, "Just because");

        verify(securityService).validateOwnerOrModerator(answer.getAuthor());
        verify(answerRepository).delete(answer);
        verify(auditLogService).logAction(eq("DELETE"), eq("ANSWER"), eq(1L), any(), eq("Just because"));
    }
}