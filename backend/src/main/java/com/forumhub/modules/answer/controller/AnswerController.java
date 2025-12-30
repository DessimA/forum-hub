package com.forumhub.modules.answer.controller;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Answers", description = "Answers and topic solutions")
public class AnswerController {
    private final AnswerService service;

    @PostMapping("/topics/{topicId}/answers")
    @Operation(summary = "Reply to a topic")
    public ResponseEntity<AnswerResponseDTO> create(@PathVariable UUID topicId, @RequestBody @Valid AnswerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(topicId, dto));
    }

    @PatchMapping("/answers/{id}/solution")
    @Operation(summary = "Toggle answer as solution (Topic author only)")
    public ResponseEntity<AnswerResponseDTO> toggleSolution(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toggleSolution(id));
    }

    @DeleteMapping("/answers/{id}")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}