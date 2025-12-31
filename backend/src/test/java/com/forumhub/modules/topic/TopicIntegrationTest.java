package com.forumhub.modules.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.dto.TopicUpdateDTO;
import com.forumhub.modules.topic.model.Topic;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TopicIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TopicRepository topicRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProfileRepository profileRepository;

    private User author;
    private Course course;

    @BeforeEach
    void setup() {
        var profile = profileRepository.findByName("ROLE_USER").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_USER")));
        author = userRepository.save(User.builder().name("Author").email("author@topic.com").password("123").profile(profile).build());
        
        var adminProfile = profileRepository.findByName("ROLE_ADMIN").orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_ADMIN")));
        userRepository.save(User.builder().name("Admin").email("admin@test.com").password("123").profile(adminProfile).build());

        course = courseRepository.save(new Course(UUID.randomUUID(), "Course", "Cat", null));
    }

    @Test
    @DisplayName("Should create topic when authenticated")
    @WithMockUser(username = "author@topic.com", roles = "USER")
    void shouldCreateTopic() throws Exception {
        var dto = new TopicRequestDTO("New Topic", "Message content", course.getId());
        
        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should update topic as owner")
    @WithMockUser(username = "author@topic.com", roles = "USER")
    void shouldUpdateTopic() throws Exception {
        var topic = topicRepository.save(Topic.builder().title("Old").message("Old").author(author).course(course).build());
        var dto = new TopicUpdateDTO("New Title", "New Msg", null);

        mockMvc.perform(put("/api/v1/topics/" + topic.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete topic as admin")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldDeleteTopic() throws Exception {
        var topic = topicRepository.save(Topic.builder().title("Del").message("Del").author(author).course(course).build());
        
        mockMvc.perform(delete("/api/v1/topics/" + topic.getId())
                        .param("justification", "Audit reason"))
                .andExpect(status().isNoContent());
    }
}
