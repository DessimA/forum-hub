package com.forumhub.modules.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService service;

    @Test
    @DisplayName("Should return 201 when create valid course")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn201WhenCreateValidCourse() throws Exception {
        var request = new CourseRequestDTO("Spring Boot", "Programming");
        var response = new CourseResponseDTO(1L, "Spring Boot", "Programming");

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }

    @Test
    @DisplayName("Should return 400 when create invalid course")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenCreateInvalidCourse() throws Exception {
        var request = new CourseRequestDTO("", "");

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
