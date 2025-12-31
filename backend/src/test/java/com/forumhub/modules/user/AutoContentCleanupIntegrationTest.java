package com.forumhub.modules.user;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.answer.model.Answer;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.security.TokenService;
import com.forumhub.modules.user.controller.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AutoContentCleanupIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TokenService tokenService;
    @Autowired private ObjectMapper objectMapper;

    private String adminToken;
    private User author;
    private Topic topic;
    private Answer answer;

    @BeforeEach
    void setUp() {
        Profile adminProfile = profileRepository.findByName("ROLE_ADMIN").orElseThrow();
        Profile userProfile = profileRepository.findByName("ROLE_USER").orElseThrow();

        User admin = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Admin").email("adm-cleanup@test.com").password("123").profile(adminProfile).build());
        adminToken = tokenService.generateToken(admin);

        author = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Author").email("author-cleanup@test.com").password("123").profile(userProfile).build());

        Course course = courseRepository.save(new Course(UUID.randomUUID(), "Test", "Test", null));

        topic = topicRepository.save(Topic.builder()
                .id(UUID.randomUUID()).title("Bad Topic").message("Bad content").author(author).course(course).build());

        answer = answerRepository.save(Answer.builder()
                .id(UUID.randomUUID()).message("Bad answer").author(author).topic(topic).build());
    }

    @Test
    void shouldDeleteTopicWhenAuthorIsPunishedWithContext() throws Exception {
        var dto = new UserController.BlockRequestDTO(false, 1, "Offensive Topic", topic.getId(), null);
        
        mockMvc.perform(post("/api/v1/users/" + author.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        assertThat(topicRepository.existsById(topic.getId())).isFalse();
        assertThat(answerRepository.existsById(answer.getId())).isFalse(); // Cascaded
    }

    @Test
    void shouldDeleteOnlyAnswerWhenAuthorIsPunishedWithAnswerContext() throws Exception {
        var dto = new UserController.BlockRequestDTO(false, 1, "Offensive Answer", null, answer.getId());
        
        mockMvc.perform(post("/api/v1/users/" + author.getId() + "/block")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        assertThat(answerRepository.existsById(answer.getId())).isFalse();
        assertThat(topicRepository.existsById(topic.getId())).isTrue(); // Topic stays
    }
}
