package com.forumhub.modules.course.controller;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Courses", description = "Course management")
public class CourseController {
    private final CourseService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course (Admin only)")
    public ResponseEntity<CourseResponseDTO> create(@RequestBody @Valid CourseRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var response = service.create(dto);
        var uri = uriBuilder.path("/api/v1/courses/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "List all courses")
    public ResponseEntity<List<CourseResponseDTO>> list() {
        return ResponseEntity.ok(service.listAll());
    }
}