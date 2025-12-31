package com.forumhub.modules.audit;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.topic.service.TopicService;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TopicDataRecoveryTest {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private UUID topicId;

    @BeforeEach
    void setUp() {
        Profile profile = profileRepository.findByName("ROLE_USER").orElseThrow();
        User user = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@recovery.com")
                .password("pass")
                .profile(profile)
                .build());

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Java");
        course.setCategory("Backend");
        course = courseRepository.save(course);

        Topic topic = new Topic();
        topic.setId(UUID.randomUUID());
        topic.setTitle("Recovery Title");
        topic.setMessage("Recovery Message");
        topic.setAuthor(user);
        topic.setCourse(course);
        topic = topicRepository.save(topic);
        topicId = topic.getId();

        Answer answer = new Answer();
        answer.setId(UUID.randomUUID());
        answer.setMessage("My important answer");
        answer.setAuthor(user);
        answer.setTopic(topic);
        answerRepository.save(answer);
    }

    @Test
    @WithMockUser(username = "test@recovery.com")
    void shouldCreateAuditLogsForAnswersWhenTopicIsDeleted() {
        // Delete topic
        topicService.delete(topicId, "Deleting for test");

        // Verify topic is gone
        assertThat(topicRepository.existsById(topicId)).isFalse();

        // Verify audit logs
        var logs = auditLogRepository.findAll();
        
        assertThat(logs).filteredOn(l -> l.getEntityType().equals("TOPIC")).hasSize(1);
        assertThat(logs).filteredOn(l -> l.getEntityType().equals("ANSWER")).hasSize(1);
        
        var answerLog = logs.stream().filter(l -> l.getEntityType().equals("ANSWER")).findFirst().get();
        assertThat(answerLog.getContent()).contains("My important answer");
    }
}