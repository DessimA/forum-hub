package com.forumhub.modules.course.controller;

import com.forumhub.modules.course.dto.CourseRequestDTO;
import com.forumhub.modules.course.dto.CourseResponseDTO;
import com.forumhub.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Courses", description = "Endpoints for managing educational courses and categories")
public class CourseController {

    private final CourseService service;

    @GetMapping
    @Operation(summary = "List all courses", description = "Retrieves a paginated list of all available courses. Publicly accessible.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of courses")
        })
    public ResponseEntity<Page<CourseResponseDTO>> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.listAll(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course", description = "Registers a new course in the database. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., missing name or category)"),
            @ApiResponse(responseCode = "403", description = "Access denied: User must have ADMIN role")
        })
    public ResponseEntity<CourseResponseDTO> create(@RequestBody @Valid CourseRequestDTO dto) {
        var response = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing course", description = "Updates the name or category of a specific course. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied: User must have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Course not found with the provided ID")
        })
    public ResponseEntity<CourseResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid CourseRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a course", description = "Permanently removes a course from the database. Requires ADMIN role.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied: User must have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Course not found with the provided ID")
        })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
