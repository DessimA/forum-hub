package com.forumhub.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "api.security.ratelimit.max-requests=10")
public class RateLimitingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEST_IP_KEY = "ratelimit:ip:127.0.0.1";

    @BeforeEach
    void setUp() {
        redisTemplate.delete(TEST_IP_KEY);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(TEST_IP_KEY);
    }

    @Test
    void shouldReturnTooManyRequestsWhenLimitExceeded() throws Exception {
        // O limite é de 10 requisições por minuto no profile de teste.
        // Esperamos 403 (Forbidden) porque o endpoint /profile é protegido.
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/v1/users/profile")).andExpect(status().isForbidden());
        }

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isTooManyRequests());
    }
}