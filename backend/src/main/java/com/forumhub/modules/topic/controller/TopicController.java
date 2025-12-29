package com.forumhub.modules.topic.controller;

import com.forumhub.modules.topic.dto.TopicDetailResponseDTO;
import com.forumhub.modules.topic.dto.TopicRequestDTO;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import com.forumhub.modules.topic.dto.TopicUpdateDTO;
import com.forumhub.modules.topic.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topics", description = "Forum topics management")
public class TopicController {
    private final TopicService service;

    @PostMapping
    @Operation(summary = "Register a new topic")
    public ResponseEntity<TopicResponseDTO> create(@RequestBody @Valid TopicRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var response = service.create(dto);
        var uri = uriBuilder.path("/api/v1/topics/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "List all topics with pagination")
    public ResponseEntity<Page<TopicResponseDTO>> list(@PageableDefault(size = 10, sort = "createdAt") Pageable pagination) {
        return ResponseEntity.ok(service.list(pagination));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic details with answers")
    public ResponseEntity<TopicDetailResponseDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update topic data")
    public ResponseEntity<TopicResponseDTO> update(@PathVariable Long id, @RequestBody @Valid TopicUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a topic")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}