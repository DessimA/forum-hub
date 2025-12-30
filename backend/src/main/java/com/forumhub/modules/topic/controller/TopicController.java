package com.forumhub.modules.topic.controller;

import com.forumhub.modules.topic.dto.*;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topics", description = "Forum topics and discussions")
public class TopicController {
    private final TopicService service;

    @PostMapping
    @Operation(summary = "Create a new topic")
    public ResponseEntity<TopicResponseDTO> create(@RequestBody @Valid TopicRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var response = service.create(dto);
        var uri = uriBuilder.path("/api/v1/topics/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "List all topics with pagination")
    public ResponseEntity<Page<TopicResponseDTO>> list(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic details including answers")
    public ResponseEntity<TopicDetailResponseDTO> getDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a topic")
    public ResponseEntity<TopicResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid TopicUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a topic (Requires justification if moderator)")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}
