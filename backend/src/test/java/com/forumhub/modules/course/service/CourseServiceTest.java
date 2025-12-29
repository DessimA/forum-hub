package com.forumhub.modules.course.service;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.mapper.CourseMapper;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository repository;

    @Mock
    private CourseMapper mapper;

    @InjectMocks
    private CourseService service;

    @Test
    @DisplayName("Should register a new course")
    void shouldCreateCourse() {
        CourseRequestDTO request = new CourseRequestDTO("Java", "Programming");
        Course course = new Course(1L, "Java", "Programming", LocalDateTime.now());
        CourseResponseDTO response = new CourseResponseDTO(1L, "Java", "Programming");

        when(mapper.toEntity(request)).thenReturn(course);
        when(repository.save(any(Course.class))).thenReturn(course);
        when(mapper.toResponseDTO(any(Course.class))).thenReturn(response);

        CourseResponseDTO result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Java");
        verify(repository, times(1)).save(any(Course.class));
    }
}
