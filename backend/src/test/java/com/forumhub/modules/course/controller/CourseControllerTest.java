package com.forumhub.modules.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.service.CourseService;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CourseService service;

    @Test
    @DisplayName("ADMIN can Create course")
    @WithMockUser(roles = "ADMIN")
    void adminCanCreate() throws Exception {
        var dto = new CourseRequestDTO("Java", "Backend");
        mockMvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("MODERATOR CANNOT Create course")
    @WithMockUser(roles = "MODERATOR")
    void moderatorCannotCreate() throws Exception {
        var dto = new CourseRequestDTO("Java", "Backend");
        mockMvc.perform(post("/api/v1/courses").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN can Update course")
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdate() throws Exception {
        UUID id = UUID.randomUUID();
        var dto = new CourseRequestDTO("Java 21", "Backend");
        mockMvc.perform(put("/api/v1/courses/" + id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMIN can Delete course")
    @WithMockUser(roles = "ADMIN")
    void adminCanDelete() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/v1/courses/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("ANYONE can List courses")
    @WithMockUser(roles = "USER")
    void anyoneCanList() throws Exception {
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());
    }
}
