package com.forumhub.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.service.UserService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    @Autowired
    private com.forumhub.modules.user.repository.UserRepository userRepository;

    @Autowired
    private com.forumhub.modules.user.repository.ProfileRepository profileRepository;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        if (userRepository.findByEmail("admin@test.com") == null) {
            var profile = profileRepository.findByName("ROLE_ADMIN").orElseThrow();
            var user = com.forumhub.modules.user.model.User.builder()
                    .name("Admin")
                    .email("admin@test.com")
                    .password("123456")
                    .profile(profile)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("Should list all users for admin")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldListAllUsersForAdmin() throws Exception {
        var userDto = new UserResponseDTO(1L, "Admin", "admin@test.com", "ROLE_ADMIN", LocalDateTime.now());
        when(service.listAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@test.com"));
    }

    @Test
    @DisplayName("Should return 403 when non-admin lists users")
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should block user for admin")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldBlockUserForAdmin() throws Exception {
        var blockReq = new UserController.BlockRequestDTO(true, 0, "Violation of rules");
        
        mockMvc.perform(post("/api/v1/users/2/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blockReq)))
                .andExpect(status().isOk());
    }
}