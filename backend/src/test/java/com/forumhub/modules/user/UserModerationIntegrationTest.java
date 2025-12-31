package com.forumhub.modules.user;

import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserModerationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private TokenService tokenService;

    private String adminToken;
    private String moderatorToken;
    private UUID targetUserId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        Profile adminProfile = profileRepository.findByName("ROLE_ADMIN").orElseThrow();
        Profile modProfile = profileRepository.findByName("ROLE_MODERATOR").orElseThrow();
        Profile userProfile = profileRepository.findByName("ROLE_USER").orElseThrow();

        User admin = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Admin").email("adm@test.com").password("123").profile(adminProfile).build());
        adminId = admin.getId();
        adminToken = tokenService.generateToken(admin);

        User mod = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Mod").email("mod@test.com").password("123").profile(modProfile).build());
        moderatorToken = tokenService.generateToken(mod);

        User target = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Target").email("user@test.com").password("123").profile(userProfile).build());
        targetUserId = target.getId();
    }

    @Test
    void moderatorShouldBeAbleToBlockAndUnblockUser() throws Exception {
        String blockJson = "{\"permanent\": false, \"days\": 1, \"justification\": \"Test\"}";
        
        // Block
        mockMvc.perform(post("/api/v1/users/" + targetUserId + "/block")
                        .header("Authorization", "Bearer " + moderatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isOk());

        // Unblock
        mockMvc.perform(post("/api/v1/users/" + targetUserId + "/unblock")
                        .header("Authorization", "Bearer " + moderatorToken))
                .andExpect(status().isOk());
    }

    @Test
    void moderatorShouldNotBeAbleToBlockAdmin() throws Exception {
        String blockJson = "{\"permanent\": true, \"justification\": \"Try to ban boss\"}";
        
        mockMvc.perform(post("/api/v1/users/" + adminId + "/block")
                        .header("Authorization", "Bearer " + moderatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldBeAbleToBlockModerator() throws Exception {
        // Create a moderator to block
        User modToBlock = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Bad Mod").email("badmod@test.com").password("123")
                .profile(profileRepository.findByName("ROLE_MODERATOR").get()).build());

        String blockJson = "{\"permanent\": true, \"justification\": \"Admin power\"}";
        
        mockMvc.perform(post("/api/v1/users/" + modToBlock.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blockJson))
                .andExpect(status().isOk());
    }
}
