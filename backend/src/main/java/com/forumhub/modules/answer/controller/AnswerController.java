package com.forumhub.modules.answer.controller;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Answers", description = "Endpoints for managing topic answers and solutions")
public class AnswerController {
    private final AnswerService service;

    @PostMapping("/topics/{topicId}/answers")
    @Operation(summary = "Reply to a topic", description = "Adds a new answer to a specific topic.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Answer posted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid answer data"),
            @ApiResponse(responseCode = "404", description = "Topic not found")
        })
    public ResponseEntity<AnswerResponseDTO> create(@PathVariable UUID topicId, @RequestBody @Valid AnswerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(topicId, dto));
    }

    @PatchMapping("/answers/{id}/solution")
    @Operation(summary = "Toggle solution status", description = "Marks or unmarks an answer as the topic solution. Only allowed for the topic author.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Solution status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the author of the topic"),
            @ApiResponse(responseCode = "404", description = "Answer not found")
        })
    public ResponseEntity<AnswerResponseDTO> toggleSolution(@PathVariable UUID id) {
        return ResponseEntity.ok(service.toggleSolution(id));
    }

    @PutMapping("/answers/{id}")
    @Operation(summary = "Update answer", description = "Updates the content of an existing answer. Only allowed for the answer author.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the author of the answer"),
            @ApiResponse(responseCode = "404", description = "Answer not found")
        })
    public ResponseEntity<AnswerResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid com.forumhub.modules.answer.dto.AnswerUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/answers/{id}")
    @Operation(summary = "Delete answer", description = "Permanently removes an answer. Requires justification if performed by a moderator.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Answer deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Answer not found")
        })
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}