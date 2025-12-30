package com.forumhub.modules.audit;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.service.AuditLogService;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuditIntegrationTest {

    @Autowired private AuditLogService auditLogService;
    @Autowired private TopicRepository topicRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ProfileRepository profileRepository;

    @Test
    @DisplayName("Should restore deleted topic from audit log with same UUID")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldRestoreTopic() {
        var role = profileRepository.findByName("ROLE_ADMIN").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_ADMIN")));
        var user = userRepository.save(User.builder().name("Admin").email("admin@test.com").password("123").profile(role).build());
        var course = courseRepository.save(new Course(UUID.randomUUID(), "Audit", "Test", null));
        var originalId = UUID.randomUUID();
        var topic = topicRepository.save(Topic.builder().id(originalId).title("To Delete").message("Msg").author(user).course(course).build());
        
        String logContent = String.format("Title: %s | Message: %s | Author: %s | CourseId: %s", 
                topic.getTitle(), topic.getMessage(), user.getEmail(), course.getId());
        
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), logContent, "Test");
        topicRepository.delete(topic);
        
        var logEntry = auditLogService.listAll().stream()
                .filter(l -> "DELETE".equals(l.getActionType()))
                .findFirst().orElseThrow();

        auditLogService.restore(logEntry.getId());

        var restored = topicRepository.findById(originalId);
        assertThat(restored).isPresent();
        assertThat(restored.get().getId()).isEqualTo(originalId);
    }
}
