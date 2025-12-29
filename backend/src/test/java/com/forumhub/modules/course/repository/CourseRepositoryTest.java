package com.forumhub.modules.course.repository;

import com.forumhub.modules.course.model.Course;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository repository;

    @Test
    @DisplayName("Should load a course by name")
    void shouldFindByName() {
        String courseName = "Spring Boot 3";
        Course course = new Course();
        course.setName(courseName);
        course.setCategory("Programming");
        repository.save(course);

        Course found = repository.findByName(courseName);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(courseName);
    }
}
