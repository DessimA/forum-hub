package com.forumhub.modules.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.model.TopicStatus;
import com.forumhub.modules.topic.repository.TopicRepository;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.answer.repository.AnswerRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TopicResolutionFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TopicRepository topicRepository;
    @Autowired private AnswerRepository answerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private CourseRepository courseRepository;

    private User author;
    private Course course;

    @BeforeEach
    void setup() {
        var profile = profileRepository.findByName("ROLE_USER").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_USER")));
        author = userRepository.save(User.builder().name("Author").email("flow-author@test.com").password("123").profile(profile).build());
        course = courseRepository.save(new Course(UUID.randomUUID(), "Flow Course", "Testing", null));
    }

    @Test
    @DisplayName("Complete Flow: Create -> Answer -> Mark Solution -> Verify Resolved")
    @WithMockUser(username = "flow-author@test.com", roles = "USER")
    void completeResolutionFlow() throws Exception {
        // 1. Create Topic
        var topicDto = new TopicRequestDTO("Flow Title", "Flow Message", course.getId());
        String topicResponse = mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(topicDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        UUID topicId = UUID.fromString(objectMapper.readTree(topicResponse).get("id").asText());

        // 2. Reply to Topic
        var answerDto = new AnswerRequestDTO("Solving answer");
        String answerResponse = mockMvc.perform(post("/api/v1/topics/" + topicId + "/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        UUID answerId = UUID.fromString(objectMapper.readTree(answerResponse).get("id").asText());

        // 3. Mark as Solution
        mockMvc.perform(patch("/api/v1/answers/" + answerId + "/solution"))
                .andExpect(status().isOk());

        // 4. Verify Topic is RESOLVED
        var resolvedTopic = topicRepository.findById(topicId).orElseThrow();
        assertThat(resolvedTopic.getStatus()).isEqualTo(TopicStatus.RESOLVED);

        // 5. Unmark Solution and Verify it returns to OPEN
        mockMvc.perform(patch("/api/v1/answers/" + answerId + "/solution"))
                .andExpect(status().isOk());
        
        var openTopic = topicRepository.findById(topicId).orElseThrow();
        assertThat(openTopic.getStatus()).isEqualTo(TopicStatus.OPEN);
    }
}