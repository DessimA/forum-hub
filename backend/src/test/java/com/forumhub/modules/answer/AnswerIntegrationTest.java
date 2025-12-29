package com.forumhub.modules.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.repository.AnswerRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AnswerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TopicRepository topicRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private AnswerRepository answerRepository;

    private Long topicId;
    private User author;

    @BeforeEach
    void setup() {
        if (profileRepository.findByName("ROLE_USER").isEmpty()) {
            profileRepository.save(new Profile(null, "ROLE_USER"));
        }
        
        var userFound = userRepository.findByEmail("author@test.com");
        if (userFound == null) {
            var profile = profileRepository.findByName("ROLE_USER").get();
            author = userRepository.save(User.builder().name("Author").email("author@test.com").password("123").profile(profile).build());
        } else {
            author = (User) userFound;
        }
        
        var course = courseRepository.save(new Course(null, "Course", "Cat", null));
        
        var topic = topicRepository.save(Topic.builder()
                .title("Topic Integration")
                .message("Message Integration")
                .author(author)
                .course(course)
                .status(TopicStatus.OPEN)
                .build());
        topicId = topic.getId();

        var answer = new com.forumhub.modules.answer.model.Answer();
        answer.setMessage("Existing Answer");
        answer.setTopic(topic);
        answer.setAuthor(author);
        answerRepository.save(answer);
    }

    @Test
    @DisplayName("Should create answer when authenticated")
    @WithMockUser(username = "author@test.com", roles = "USER")
    void shouldCreateAnswer() throws Exception {
        var dto = new AnswerRequestDTO("My new answer");
        
        mockMvc.perform(post("/api/v1/topics/" + topicId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 403 when not authenticated")
    void shouldReturnUnauthorized() throws Exception {
        var dto = new AnswerRequestDTO("My new answer");
        
        mockMvc.perform(post("/api/v1/topics/" + topicId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should mark solution as topic author")
    @WithMockUser(username = "author@test.com", roles = "USER")
    void shouldMarkSolution() throws Exception {
        var existingAnswer = answerRepository.findAll().get(0);
        mockMvc.perform(patch("/api/v1/answers/" + existingAnswer.getId() + "/solution"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete answer")
    @WithMockUser(username = "author@test.com", roles = "USER")
    void shouldDeleteAnswer() throws Exception {
        var existingAnswer = answerRepository.findAll().get(0);
        mockMvc.perform(delete("/api/v1/answers/" + existingAnswer.getId())
                        .param("justification", "Deleted by author"))
                .andExpect(status().isNoContent());
    }
}