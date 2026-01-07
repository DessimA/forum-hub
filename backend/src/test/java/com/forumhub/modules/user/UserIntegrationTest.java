package com.forumhub.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.user.controller.UserController;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.favorite.repository.UserFavoriteRepository;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import com.forumhub.modules.notification.repository.NotificationRepository;
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

import java.util.UUID;

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
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UserFavoriteRepository favoriteRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private NotificationRepository notificationRepository;

    private User adminUser;
    private User moderatorUser;
    private User regularUser;

    @BeforeEach
    void setup() {
        // Clean up all data to avoid FK violations and ensure isolation
        auditLogRepository.deleteAll();
        notificationRepository.deleteAll();
        favoriteRepository.deleteAll();
        answerRepository.deleteAll();
        topicRepository.deleteAll();
        userRepository.deleteAll();
        
        var adminProfile = profileRepository.findByName("ROLE_ADMIN").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_ADMIN")));
        var modProfile = profileRepository.findByName("ROLE_MODERATOR").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_MODERATOR")));
        var userProfile = profileRepository.findByName("ROLE_USER").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_USER")));

        adminUser = userRepository.save(User.builder().name("Admin").email("integration-admin@test.com").password("123").profile(adminProfile).build());
        moderatorUser = userRepository.save(User.builder().name("Mod").email("integration-mod@test.com").password("123").profile(modProfile).build());
        regularUser = userRepository.save(User.builder().name("User").email("integration-user@test.com").password("123").profile(userProfile).build());
    }

    @Test
    @DisplayName("ADMIN should block a USER")
    @WithMockUser(username = "integration-admin@test.com", roles = "ADMIN")
    void adminShouldBlockUser() throws Exception {
        var dto = new UserController.BlockRequestDTO(true, null, "Spam", null, null);
        mockMvc.perform(post("/api/v1/users/" + regularUser.getId() + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("MODERATOR should NOT block an ADMIN")
    @WithMockUser(username = "integration-mod@test.com", roles = "MODERATOR")
    void moderatorShouldNotBlockAdmin() throws Exception {
        var dto = new UserController.BlockRequestDTO(false, 1, "Testing", null, null);
        mockMvc.perform(post("/api/v1/users/" + adminUser.getId() + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MODERATOR should NOT perform permanent block")
    @WithMockUser(username = "integration-mod@test.com", roles = "MODERATOR")
    void moderatorShouldNotPermanentBlock() throws Exception {
        var dto = new UserController.BlockRequestDTO(true, null, "Abuse", null, null);
        mockMvc.perform(post("/api/v1/users/" + regularUser.getId() + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("USER should NOT block anyone")
    @WithMockUser(username = "integration-user@test.com", roles = "USER")
    void userShouldNotBlockAnyone() throws Exception {
        var dto = new UserController.BlockRequestDTO(false, 1, "Hack", null, null);
        mockMvc.perform(post("/api/v1/users/" + moderatorUser.getId() + "/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN should unblock a user")
    @WithMockUser(username = "integration-admin@test.com", roles = "ADMIN")
    void adminShouldUnblockUser() throws Exception {
        mockMvc.perform(post("/api/v1/users/" + regularUser.getId() + "/unblock"))
                .andExpect(status().isOk());
    }
}
