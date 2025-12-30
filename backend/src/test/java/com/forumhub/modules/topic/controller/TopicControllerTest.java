package com.forumhub.modules.topic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.topic.dto.TopicDetailResponseDTO;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.service.TopicService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TopicControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private TopicService service;

    @Test
    @DisplayName("Should return 201 when create valid topic")
    @WithMockUser(roles = "USER")
    void shouldCreateTopic() throws Exception {
        UUID courseId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();
        var dto = new TopicRequestDTO("Java", "Message", courseId);
        var response = new TopicResponseDTO(topicId, "Java", "Message", LocalDateTime.now(), TopicStatus.OPEN, "Author", UUID.randomUUID(), "a@t.com", "Course");
        
        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 200 when get detail")
    @WithMockUser(roles = "USER")
    void shouldGetDetail() throws Exception {
        UUID topicId = UUID.randomUUID();
        var detail = new TopicDetailResponseDTO(topicId, "T", "M", LocalDateTime.now(), TopicStatus.OPEN, "A", UUID.randomUUID(), "e", "C", Collections.emptyList());
        
        when(service.getDetail(topicId)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/topics/" + topicId))
                .andExpect(status().isOk());
    }
}