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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TopicFilteringIntegrationTest {

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

    private UUID course1Id;
    private UUID course2Id;

    @BeforeEach
    void setUp() {
        Profile userProfile = profileRepository.findByName("ROLE_USER")
                .orElseGet(() -> profileRepository.save(new Profile(UUID.randomUUID(), "ROLE_USER")));
        
        User author = userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name("Author Test")
                .email("author@test.com")
                .password("password")
                .profile(userProfile)
                .build());

        Course c1 = new Course();
        c1.setId(UUID.randomUUID());
        c1.setName("Spring Boot");
        c1.setCategory("Backend");
        c1 = courseRepository.save(c1);

        Course c2 = new Course();
        c2.setId(UUID.randomUUID());
        c2.setName("Vue.js");
        c2.setCategory("Frontend");
        c2 = courseRepository.save(c2);
        course1Id = c1.getId();
        course2Id = c2.getId();

        topicRepository.save(createTopic("Topic 1", c1, author, TopicStatus.OPEN));
        topicRepository.save(createTopic("Topic 2", c1, author, TopicStatus.RESOLVED));
        topicRepository.save(createTopic("Topic 3", c2, author, TopicStatus.OPEN));
    }

    private Topic createTopic(String title, Course course, User author, TopicStatus status) {
        Topic t = new Topic();
        t.setId(UUID.randomUUID());
        t.setTitle(title);
        t.setMessage("Message content");
        t.setCourse(course);
        t.setAuthor(author);
        t.setStatus(status);
        return t;
    }

    @Test
    @WithMockUser
    void shouldFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/topics").param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Topic 2"));
    }

    @Test
    @WithMockUser
    void shouldFilterByCourse() throws Exception {
        mockMvc.perform(get("/api/v1/topics").param("courseId", course1Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @WithMockUser
    void shouldFilterByCourseAndStatus() throws Exception {
        mockMvc.perform(get("/api/v1/topics")
                        .param("courseId", course1Id.toString())
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Topic 1"));
    }

    @Test
    @WithMockUser
    void shouldFilterByTitle() throws Exception {
        mockMvc.perform(get("/api/v1/topics").param("title", "topic 3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Topic 3"));
    }
}
