package com.forumhub.security;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlockedUserSecurityTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private CourseRepository courseRepository;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 403 when a PERMANENTLY blocked user tries to post")
    @WithMockUser(username = "blocked@test.com")
    void permanentlyBlockedUserCannotPost() throws Exception {
        UUID courseId = UUID.randomUUID();
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("blocked@test.com")
                .isPermanentBlocked(true)
                .build();
        
        when(userRepository.findByEmail("blocked@test.com")).thenReturn(java.util.Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new Course(courseId, "C", "C", null)));

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TopicRequestDTO("Title", "Msg", courseId))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when a TEMPORARILY suspended user tries to post")
    @WithMockUser(username = "suspended@test.com")
    void suspendedUserCannotPost() throws Exception {
        UUID courseId = UUID.randomUUID();
        var user = User.builder()
                .id(UUID.randomUUID())
                .email("suspended@test.com")
                .blockedUntil(LocalDateTime.now().plusDays(1))
                .build();
        
        when(userRepository.findByEmail("suspended@test.com")).thenReturn(java.util.Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new Course(courseId, "C", "C", null)));

        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TopicRequestDTO("Title", "Msg", courseId))))
                .andExpect(status().isForbidden());
    }
}
