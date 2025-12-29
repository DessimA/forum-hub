package com.forumhub.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.user.controller.UserController;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;

    @BeforeEach
    void setup() {
        if (profileRepository.findByName("ROLE_USER").isEmpty()) {
            profileRepository.save(new Profile(null, "ROLE_USER"));
        }
        if (profileRepository.findByName("ROLE_ADMIN").isEmpty()) {
            profileRepository.save(new Profile(null, "ROLE_ADMIN"));
        }
        
        if (userRepository.findByEmail("test@user.com") == null) {
            var profile = profileRepository.findByName("ROLE_USER").get();
            var user = User.builder().name("Test User").email("test@user.com").password("123").profile(profile).build();
            userRepository.save(user);
        }

        if (userRepository.findByEmail("admin@forumhub.com") == null) {
            var profile = profileRepository.findByName("ROLE_ADMIN").get();
            var user = User.builder().name("Admin").email("admin@forumhub.com").password("123").profile(profile).build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("Should get profile when authenticated")
    @WithMockUser(username = "test@user.com", roles = "USER")
    void shouldGetProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should update profile")
    @WithMockUser(username = "test@user.com", roles = "USER")
    void shouldUpdateProfile() throws Exception {
        var dto = new UserUpdateDTO("New Name", "test@user.com", null);
        
        mockMvc.perform(put("/api/v1/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should list all users as admin")
    @WithMockUser(username = "admin@forumhub.com", roles = "ADMIN")
    void shouldListAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 403 when listing users as regular user")
    @WithMockUser(username = "test@user.com", roles = "USER")
    void shouldNotListAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should block user as admin")
    @WithMockUser(username = "admin@forumhub.com", roles = "ADMIN")
    void shouldBlockUser() throws Exception {
        var userToBlock = (User) userRepository.findByEmail("test@user.com");
        var dto = new UserController.BlockRequestDTO(true, null, "Spam");
        
        mockMvc.perform(post("/api/v1/users/" + userToBlock.getId() + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
