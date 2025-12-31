package com.forumhub.modules.answer;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnswerUpdateIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TokenService tokenService;
    @Autowired private ObjectMapper objectMapper;

    private String authorToken;
    private String otherUserToken;
    private UUID answerId;

    @BeforeEach
    void setUp() {
        Profile userProfile = profileRepository.findByName("ROLE_USER").orElseThrow();

        User author = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Author").email("ans-author@test.com").password("123").profile(userProfile).build());
        authorToken = tokenService.generateToken(author);

        User other = userRepository.save(User.builder()
                .id(UUID.randomUUID()).name("Other").email("ans-other@test.com").password("123").profile(userProfile).build());
        otherUserToken = tokenService.generateToken(other);

        Course course = courseRepository.save(new Course(UUID.randomUUID(), "Test", "Test", null));
        Topic topic = topicRepository.save(Topic.builder()
                .id(UUID.randomUUID()).title("Topic").message("Msg").author(author).course(course).build());

        Answer answer = answerRepository.save(Answer.builder()
                .id(UUID.randomUUID()).message("Original answer").author(author).topic(topic).build());
        answerId = answer.getId();
    }

    @Test
    void authorShouldBeAbleToUpdateAnswer() throws Exception {
        String updateJson = "{\"message\": \"Updated answer message\"}";

        mockMvc.perform(put("/api/v1/answers/" + answerId)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Updated answer message"));
    }

    @Test
    void otherUserShouldNotBeAbleToUpdateAnswer() throws Exception {
        String updateJson = "{\"message\": \"Hacked answer\"}";

        mockMvc.perform(put("/api/v1/answers/" + answerId)
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }
}
