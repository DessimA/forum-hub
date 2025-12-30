package com.forumhub.modules.answer.service;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.topic.service.TopicValidationService;
import com.forumhub.exception.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerBusinessRulesTest {

    @Mock private TopicRepository topicRepository;
    @Mock private TopicValidationService validationService;
    @InjectMocks private AnswerService answerService;

    @Test
    @DisplayName("Should throw exception when replying to a CLOSED topic")
    void shouldThrowWhenTopicClosed() {
        var topicId = UUID.randomUUID();
        var topic = Topic.builder().id(topicId).status(TopicStatus.CLOSED).build();
        
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        
        doThrow(new BusinessValidationException("Topic is closed"))
            .when(validationService).validateTopicOpen(topic);

        assertThatThrownBy(() -> answerService.create(topicId, new AnswerRequestDTO("msg")))
                .isInstanceOf(BusinessValidationException.class);
    }
}
