package com.forumhub.modules.course.service;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.mapper.CourseMapper;
import com.forumhub.modules.course.model.Course;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock private CourseRepository repository;
    @Mock private CourseMapper mapper;
    @InjectMocks private CourseService service;

    @Test
    @DisplayName("Should create a new course")
    void shouldCreateCourse() {
        var dto = new CourseRequestDTO("Java", "Backend");
        var course = new Course(UUID.randomUUID(), "Java", "Backend", null);
        var response = new CourseResponseDTO(course.getId(), "Java", "Backend");

        when(mapper.toEntity(dto)).thenReturn(course);
        when(repository.save(any(Course.class))).thenReturn(course);
        when(mapper.toResponse(course)).thenReturn(response);

        var result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Java");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should update an existing course")
    void shouldUpdateCourse() {
        var id = UUID.randomUUID();
        var dto = new CourseRequestDTO("Java 17", "Backend");
        var existing = new Course(id, "Java", "Backend", null);
        
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Course.class))).thenReturn(existing);
        when(mapper.toResponse(any())).thenReturn(new CourseResponseDTO(id, "Java 17", "Backend"));

        var result = service.update(id, dto);

        assertThat(result.name()).isEqualTo("Java 17");
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should delete a course")
    void shouldDeleteCourse() {
        var id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when course not found on update")
    void shouldThrowOnUpdateNotFound() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        var dto = new CourseRequestDTO("Java", "Backend");

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should list all courses with pagination")
    void shouldListAllCourses() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var course = new Course(UUID.randomUUID(), "Java", "Backend", null);
        var page = new org.springframework.data.domain.PageImpl<>(java.util.List.of(course));

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(course)).thenReturn(new CourseResponseDTO(course.getId(), "Java", "Backend"));

        var result = service.listAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(repository).findAll(pageable);
    }
}