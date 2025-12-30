package com.forumhub.modules.course.mapper;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Course toEntity(CourseRequestDTO dto);
    
    CourseResponseDTO toResponse(Course course);
}
