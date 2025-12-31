package com.forumhub.modules.topic;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TopicClosureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TokenService tokenService;

    private String authorToken;
    private String otherUserToken;
    private UUID topicId;

    @BeforeEach
    void setUp() {
        Profile userProfile = profileRepository.findByName("ROLE_USER").orElseThrow();
        
        User author = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Author")
                .email("author@test.com")
                .password("password")
                .profile(userProfile)
                .build());
        authorToken = tokenService.generateToken(author);

        User other = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Other")
                .email("other@test.com")
                .password("password")
                .profile(userProfile)
                .build());
        otherUserToken = tokenService.generateToken(other);

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Java");
        course.setCategory("Backend");
        course = courseRepository.save(course);

        Topic topic = new Topic();
        topic.setId(UUID.randomUUID());
        topic.setTitle("Test Topic");
        topic.setMessage("Content");
        topic.setAuthor(author);
        topic.setCourse(course);
        topic.setStatus(TopicStatus.OPEN);
        topicId = topicRepository.save(topic).getId();
    }

    @Test
    void authorShouldBeAbleToCloseTopic() throws Exception {
        mockMvc.perform(patch("/api/v1/topics/" + topicId + "/close")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void authorShouldBeAbleToReopenTopic() throws Exception {
        // First close it
        topicRepository.findById(topicId).ifPresent(t -> {
            t.setStatus(TopicStatus.CLOSED);
            topicRepository.save(t);
        });

        mockMvc.perform(patch("/api/v1/topics/" + topicId + "/reopen")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void otherUserShouldNotBeAbleToCloseTopic() throws Exception {
        mockMvc.perform(patch("/api/v1/topics/" + topicId + "/close")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotAllowAnsweringClosedTopic() throws Exception {
        // First close the topic
        mockMvc.perform(patch("/api/v1/topics/" + topicId + "/close")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk());

        // Try to answer
        String answerJson = "{\"message\": \"Test answer\"}";
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/topics/" + topicId + "/answers")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(answerJson))
                .andExpect(status().isBadRequest()); // BusinessValidationException usually returns 400
    }
}
