package com.forumhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("none")
public class MockRedisConfig {
    // Arquivo mantido vazio e desativado para permitir uso do Redis real no Docker
}
