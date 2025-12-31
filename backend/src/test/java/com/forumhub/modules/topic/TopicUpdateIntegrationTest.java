package com.forumhub.modules.topic;

import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.modules.topic.model.Topic;
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
class TopicUpdateIntegrationTest {

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
    private UUID topicId;
    private UUID newCourseId;

    @BeforeEach
    void setUp() {
        Profile userProfile = profileRepository.findByName("ROLE_USER").orElseThrow();
        User author = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Author")
                .email("editor@test.com")
                .password("password")
                .profile(userProfile)
                .build());
        authorToken = tokenService.generateToken(author);

        Course c1 = new Course();
        c1.setId(UUID.randomUUID());
        c1.setName("Old Course");
        c1.setCategory("Test");
        c1 = courseRepository.save(c1);

        Course c2 = new Course();
        c2.setId(UUID.randomUUID());
        c2.setName("New Course");
        c2.setCategory("Test");
        c2 = courseRepository.save(c2);
        newCourseId = c2.getId();

        Topic topic = new Topic();
        topic.setId(UUID.randomUUID());
        topic.setTitle("Original Title");
        topic.setMessage("Original Message");
        topic.setAuthor(author);
        topic.setCourse(c1);
        topicId = topicRepository.save(topic).getId();
    }

    @Test
    void shouldUpdateTopicCourse() throws Exception {
        String updateJson = String.format("{\"title\": \"Updated\", \"message\": \"Updated\", \"courseId\": \"%s\"}", newCourseId);

        mockMvc.perform(put("/api/v1/topics/" + topicId)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value("New Course"));
    }
}
