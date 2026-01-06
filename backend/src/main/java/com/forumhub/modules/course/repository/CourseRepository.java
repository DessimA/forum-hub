package com.forumhub.modules.course.repository;

import com.forumhub.modules.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Course findByName(String name);
    boolean existsByName(String name);
}