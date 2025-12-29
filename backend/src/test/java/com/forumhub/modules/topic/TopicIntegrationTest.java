package com.forumhub.modules.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TopicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private com.forumhub.modules.user.repository.UserRepository userRepository;

    @Autowired
    private com.forumhub.modules.user.repository.ProfileRepository profileRepository;

    private Long courseId;

    @BeforeEach
    void setup() {
        var course = new Course(null, "Test Course", "Test Category", null);
        courseId = courseRepository.save(course).getId();

        if (userRepository.findByEmail("admin@forumhub.com") == null) {
            var profile = profileRepository.findByName("ROLE_ADMIN").orElseThrow();
            var user = com.forumhub.modules.user.model.User.builder()
                    .name("Admin")
                    .email("admin@forumhub.com")
                    .password("123456")
                    .profile(profile)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("Should return unauthorized for guest")
    void shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/topics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should create topic when authenticated")
    @WithMockUser(username = "admin@forumhub.com", roles = "ADMIN")
    void shouldCreateTopic() throws Exception {
        var dto = new TopicRequestDTO("Test Title", "Test Message content", courseId);
        
        mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}