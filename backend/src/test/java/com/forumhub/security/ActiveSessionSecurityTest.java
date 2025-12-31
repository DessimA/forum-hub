package com.forumhub.security;

import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ActiveSessionSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private TokenService tokenService;

    private String validToken;
    private User user;

    @BeforeEach
    void setUp() {
        Profile profile = profileRepository.findByName("ROLE_USER").orElseThrow();
        user = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Victim").email("victim@test.com").password("123").profile(profile).build());
        validToken = tokenService.generateToken(user);
    }

    @Test
    void shouldDenyAccessToValidTokenIfUserIsBlockedDuringSession() throws Exception {
        // 1. Works fine initially
        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        // 2. Admin blocks user in DB
        user.setIsPermanentBlocked(true);
        userRepository.save(user);

        // 3. Same token should now be rejected
        mockMvc.perform(get("/api/v1/users/profile")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());
    }
}
