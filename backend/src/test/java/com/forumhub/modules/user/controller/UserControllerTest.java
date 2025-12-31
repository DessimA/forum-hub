package com.forumhub.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.service.UserService;
import com.forumhub.security.SecurityValidationService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService service;
    @MockitoBean private SecurityValidationService securityService;

    @Test
    @DisplayName("Should list all users for admin")
    @WithMockUser(roles = "ADMIN")
    void shouldListAllUsersForAdmin() throws Exception {
        when(service.listAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return forbidden for non-admin")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should block user for admin")
    @WithMockUser(roles = "ADMIN")
    void shouldBlockUserForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        var dto = new UserController.BlockRequestDTO(true, 0, "Violation of rules", null, null);
        var mockTarget = User.builder().id(id).email("target@test.com").profile(new Profile(UUID.randomUUID(), "ROLE_USER")).build();
        var mockResponse = new UserResponseDTO(id, "Target", "target@test.com", "ROLE_USER", LocalDateTime.now(), false, null);

        when(securityService.isResourceOwner(id)).thenReturn(false);
        when(service.findById(id)).thenReturn(mockTarget);
        when(service.toDTO(mockTarget)).thenReturn(mockResponse);
        when(securityService.hasRole("ROLE_ADMIN")).thenReturn(true);

        mockMvc.perform(post("/api/v1/users/" + id + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}