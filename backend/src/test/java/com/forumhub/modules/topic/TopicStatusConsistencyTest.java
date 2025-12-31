package com.forumhub.modules.topic;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.answer.service.AnswerService;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TopicStatusConsistencyTest {

    @Autowired private AnswerService answerService;
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private CourseRepository courseRepository;

    private UUID topicId;
    private UUID answerId;

    @BeforeEach
    void setUp() {
        Profile profile = profileRepository.findByName("ROLE_USER").orElseThrow();
        User author = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Author").email("status@test.com").password("123").profile(profile).build());

        Course course = courseRepository.save(new Course(UUID.randomUUID(), "Test", "Test", null));
        Topic topic = topicRepository.save(Topic.builder()
                .id(UUID.randomUUID()).title("Status Topic").message("Msg").author(author).course(course).build());
        topicId = topic.getId();

        Answer answer = answerRepository.save(Answer.builder()
                .id(UUID.randomUUID()).message("The Solution").author(author).topic(topic).solution(false).build());
        answerId = answer.getId();
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "status@test.com")
    void shouldRevertTopicToOpenWhenSolutionIsUnmarked() {
        // 1. Mark as solution
        answerService.toggleSolution(answerId);
        assertThat(topicRepository.findById(topicId).get().getStatus()).isEqualTo(TopicStatus.RESOLVED);

        // 2. Unmark as solution
        answerService.toggleSolution(answerId);
        assertThat(topicRepository.findById(topicId).get().getStatus()).isEqualTo(TopicStatus.OPEN);
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "status@test.com")
    void shouldRevertTopicToOpenWhenSolutionAnswerIsDeleted() {
        // 1. Mark as solution
        answerService.toggleSolution(answerId);
        assertThat(topicRepository.findById(topicId).get().getStatus()).isEqualTo(TopicStatus.RESOLVED);

        // 2. Delete the answer
        answerService.delete(answerId, "Testing revert");
        assertThat(topicRepository.findById(topicId).get().getStatus()).isEqualTo(TopicStatus.OPEN);
    }
}
