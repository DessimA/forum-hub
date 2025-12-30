package com.forumhub.config;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Fixed UUIDs as defined in V2 migration
        UUID adminProfileId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID moderatorProfileId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID userProfileId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

        var adminProfile = profileRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> profileRepository.save(new Profile(adminProfileId, "ROLE_ADMIN")));
        
        profileRepository.findByName("ROLE_MODERATOR")
                .orElseGet(() -> profileRepository.save(new Profile(moderatorProfileId, "ROLE_MODERATOR")));

        profileRepository.findByName("ROLE_USER")
                .orElseGet(() -> profileRepository.save(new Profile(userProfileId, "ROLE_USER")));

        if (userRepository.findByEmail("admin@forumhub.com").isEmpty()) {
            var admin = User.builder()
                    .name("Administrator")
                    .email("admin@forumhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .profile(adminProfile)
                    .isPermanentBlocked(false)
                    .build();
            userRepository.save(admin);
        }
    }
}