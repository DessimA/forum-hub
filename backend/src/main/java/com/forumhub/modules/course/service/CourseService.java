package com.forumhub.modules.course.service;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.mapper.CourseMapper;
import com.forumhub.modules.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository repository;
    private final CourseMapper mapper;

    @Transactional
    public CourseResponseDTO create(@NonNull CourseRequestDTO dto) {
        var course = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(course));
    }

    public List<CourseResponseDTO> listAll() {
        return repository.findAll().stream().map(mapper::toResponseDTO).toList();
    }
}