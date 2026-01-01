package com.forumhub.modules.topic.controller;

import com.forumhub.modules.topic.dto.*;
import com.forumhub.modules.topic.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topics", description = "Forum topics and discussions management")
public class TopicController {
    private final TopicService service;

    @PostMapping
    @Operation(summary = "Create a new topic", description = "Creates a new discussion topic and returns its data.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Topic created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
        })
    public ResponseEntity<TopicResponseDTO> create(@RequestBody @Valid TopicRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var response = service.create(dto);
        var uri = uriBuilder.path("/api/v1/topics/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "List topics", description = "Lists all topics with pagination and optional filters (course, status, year, title).",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved topics")
        })
    public ResponseEntity<Page<TopicResponseDTO>> list(
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable,
            @RequestParam(required = false) java.util.UUID courseId,
            @RequestParam(required = false) com.forumhub.modules.topic.model.TopicStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(service.list(pageable, courseId, status, year, title));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic detail", description = "Retrieves full details of a specific topic, including all its answers.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Topic details found"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<TopicDetailResponseDTO> getDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update topic", description = "Updates the title and/or message of an existing topic. Only allowed for authors or moderators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Topic updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not authorized to update this topic"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<TopicResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid TopicUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close topic", description = "Sets topic status to CLOSED. Only allowed for authors or moderators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Topic closed successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<TopicResponseDTO> close(@PathVariable UUID id) {
        return ResponseEntity.ok(service.close(id));
    }

    @PatchMapping("/{id}/reopen")
    @Operation(summary = "Reopen topic", description = "Sets topic status to OPEN. Only allowed for authors or moderators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Topic reopened successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<TopicResponseDTO> reopen(@PathVariable UUID id) {
        return ResponseEntity.ok(service.reopen(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete topic", description = "Removes a topic permanently. Moderators must provide a justification.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Topic deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}
