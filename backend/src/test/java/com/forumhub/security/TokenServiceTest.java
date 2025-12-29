package com.forumhub.security;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-123456789");
    }

    @Test
    @DisplayName("Should generate a valid token for a user")
    void shouldGenerateValidToken() {
        User user = User.builder()
                .id(1L)
                .email("test@forumhub.com")
                .profile(new Profile(1L, "ROLE_USER"))
                .build();

        String token = tokenService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(tokenService.getSubject(token)).isEqualTo("test@forumhub.com");
    }
}