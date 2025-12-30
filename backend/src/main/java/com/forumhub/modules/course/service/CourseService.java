package com.forumhub.modules.course.service;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.mapper.CourseMapper;
import com.forumhub.modules.course.repository.CourseRepository;
import com.forumhub.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository repository;
    private final CourseMapper mapper;

    @Transactional
    public CourseResponseDTO create(CourseRequestDTO dto) {
        log.info("Creating course: {}", dto.name());
        var course = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(course));
    }

    public Page<CourseResponseDTO> listAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public CourseResponseDTO update(UUID id, CourseRequestDTO dto) {
        log.info("Updating course ID: {}", id);
        var course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        course.setName(dto.name());
        course.setCategory(dto.category());
        
        return mapper.toResponse(repository.save(course));
    }

    @Transactional
    public void delete(UUID id) {
        log.warn("Deleting course ID: {}", id);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found");
        }
        repository.deleteById(id);
    }
}
