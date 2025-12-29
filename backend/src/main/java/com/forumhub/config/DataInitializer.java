package com.forumhub.config;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@forumhub.com") == null) {
            Profile adminProfile = profileRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> profileRepository.save(new Profile(null, "ROLE_ADMIN")));
            
            if (profileRepository.findByName("ROLE_USER").isEmpty()) {
                profileRepository.save(new Profile(null, "ROLE_USER"));
            }

            User admin = User.builder()
                    .name("Administrator")
                    .email("admin@forumhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .profile(adminProfile)
                    .build();
            
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@forumhub.com / admin123");
        }
    }
}
