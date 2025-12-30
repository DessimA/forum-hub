package com.forumhub.security;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService service;

    @BeforeEach
    void setUp() {
        service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "test_secret");
    }

    @Test
    @DisplayName("Should generate a valid token")
    void shouldGenerateToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .profile(new Profile(UUID.randomUUID(), "ROLE_USER"))
                .build();

        String token = service.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(service.getSubject(token)).isEqualTo("user@test.com");
    }
}
