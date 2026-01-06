package com.forumhub.modules.audit;

import com.forumhub.modules.audit.model.AuditLog;
import com.forumhub.modules.audit.service.AuditLogService;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.exception.BusinessValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AuditRecoveryScenarioTest {

    @Autowired private AuditLogService auditLogService;
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ProfileRepository profileRepository;

    private User author;
    private Course course;

    @BeforeEach
    void setup() {
        var profile = profileRepository.findByName("ROLE_USER").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_USER")));
        author = userRepository.save(User.builder().name("Author").email("recovery@test.com").password("123").profile(profile).build());
        course = courseRepository.save(new Course(UUID.randomUUID(), "Recovery Course", "Testing", null));
    }

    @Test
    @DisplayName("Should only allow answer restoration after its parent topic is restored")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void complexRestorationScenario() {
        // 1. Setup: Create Topic and Answer
        var topicId = UUID.randomUUID();
        var topic = topicRepository.save(Topic.builder().id(topicId).title("Parent").message("Body").author(author).course(course).build());
        
        var answerId = UUID.randomUUID();
        var answer = answerRepository.save(Answer.builder().id(answerId).message("Child Message").author(author).topic(topic).build());
        
        // 2. Delete Child (Answer) then Parent (Topic)
        String ansLog = String.format("Message: %s | Author: %s | TopicId: %s", answer.getMessage(), author.getEmail(), topic.getId().toString());
        auditLogService.logAction("DELETE", "ANSWER", answer.getId(), ansLog, "Reason");
        answerRepository.delete(answer);

        String topLog = String.format("Title: %s | Message: %s | Author: %s | CourseId: %s", topic.getTitle(), topic.getMessage(), author.getEmail(), course.getId().toString());
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), topLog, "Reason");
        topicRepository.delete(topic);

        // Get log IDs
        var logs = auditLogService.listAll();
        var answerLog = logs.stream().filter(l -> "ANSWER".equals(l.getEntityType())).findFirst().orElseThrow();
        var topicLog = logs.stream().filter(l -> "TOPIC".equals(l.getEntityType())).findFirst().orElseThrow();

        // 3. Try to restore Answer first -> SHOULD FAIL
        assertThatThrownBy(() -> auditLogService.restore(answerLog.getId(), false))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Original topic");

        // 4. Restore Topic -> SHOULD SUCCEED
        auditLogService.restore(topicLog.getId(), false);
        assertThat(topicRepository.findAll()).hasSize(1);

        // 5. Try to restore Answer again -> SHOULD NOW SUCCEED
        auditLogService.restore(answerLog.getId(), false);
        
        var restoredAnswers = answerRepository.findAll();
        assertThat(restoredAnswers).hasSize(1);
        assertThat(restoredAnswers.get(0).getMessage()).isEqualTo("Child Message");
        assertThat(restoredAnswers.get(0).getTopic().getTitle()).isEqualTo("Parent");
    }

    @Test
    @DisplayName("Should restore topic and all answers recursively")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void recursiveRestorationScenario() {
        // 1. Setup
        var topicId = UUID.randomUUID();
        var topic = topicRepository.save(Topic.builder().id(topicId).title("Parent").message("Body").author(author).course(course).build());
        
        var answerId = UUID.randomUUID();
        var answer = answerRepository.save(Answer.builder().id(answerId).message("Recursive Child").author(author).topic(topic).build());
        
        // 2. Delete both
        String ansLog = String.format("Message: %s | Author: %s | TopicId: %s", answer.getMessage(), author.getEmail(), topic.getId().toString());
        auditLogService.logAction("DELETE", "ANSWER", answer.getId(), ansLog, "Recursive Test");
        answerRepository.delete(answer);

        String topLog = String.format("Title: %s | Message: %s | Author: %s | CourseId: %s", topic.getTitle(), topic.getMessage(), author.getEmail(), course.getId().toString());
        auditLogService.logAction("DELETE", "TOPIC", topic.getId(), topLog, "Recursive Test");
        topicRepository.delete(topic);

        var topicLog = auditLogService.listAll().stream()
                .filter(l -> "TOPIC".equals(l.getEntityType()))
                .findFirst().orElseThrow();

        // 3. Restore TOPIC with includeAnswers = true
        auditLogService.restore(topicLog.getId(), true);

        // 4. Verify both are back
        assertThat(topicRepository.findById(topicId)).isPresent();
        assertThat(answerRepository.findById(answerId)).isPresent();
        assertThat(auditLogService.listAll()).isEmpty(); // Logs should be purged after restoration
    }
}
