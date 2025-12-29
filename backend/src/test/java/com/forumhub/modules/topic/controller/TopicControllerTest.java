package com.forumhub.modules.topic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TopicService service;

    @Test
    @DisplayName("Should create topic successfully")
    @WithMockUser(username = "user@test.com", roles = "USER")
    void shouldCreateTopic() throws Exception {
        var request = new TopicRequestDTO("Clean Code", "About clean code", 1L);
        var response = new TopicResponseDTO(1L, "Clean Code", "About clean code", LocalDateTime.now(), TopicStatus.OPEN, "Author", 1L, "user@test.com", "Java");

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }
}
