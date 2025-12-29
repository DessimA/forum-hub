package com.forumhub.config;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            var adminProfile = profileRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found. Ensure Flyway migrations ran correctly."));
            
            var admin = User.builder()
                    .name("Administrator")
                    .email("admin@forumhub.com")
                    .password(passwordEncoder.encode("admin123"))
                    .profile(adminProfile)
                    .build();

            userRepository.save(admin);
        }

        if (courseRepository.count() == 0) {
            var courses = List.of(
                new Course(null, "Spring Boot 3", "Backend", null),
                new Course(null, "Vue.js 3", "Frontend", null),
                new Course(null, "Docker Basics", "DevOps", null)
            );
            courseRepository.saveAll(courses);
        }
    }
}
