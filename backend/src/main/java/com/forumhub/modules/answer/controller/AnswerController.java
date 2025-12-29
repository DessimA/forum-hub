package com.forumhub.modules.answer.controller;

import com.forumhub.modules.answer.dto.AnswerRequestDTO;
import com.forumhub.modules.answer.dto.AnswerResponseDTO;
import com.forumhub.modules.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Answers", description = "Answers and solutions management")
public class AnswerController {
    private final AnswerService service;

    @PostMapping("/topics/{topicId}/answers")
    @Operation(summary = "Reply to a topic")
    public ResponseEntity<AnswerResponseDTO> reply(@PathVariable Long topicId, @RequestBody @Valid AnswerRequestDTO dto, UriComponentsBuilder uriBuilder) {
        var response = service.reply(topicId, dto);
        var uri = uriBuilder.path("/api/v1/answers/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PatchMapping("/answers/{id}/solution")
    @Operation(summary = "Mark/unmark an answer as solution")
    public ResponseEntity<AnswerResponseDTO> markAsSolution(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsSolution(id));
    }

    @DeleteMapping("/answers/{id}")
    @Operation(summary = "Delete an answer")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) String justification) {
        service.delete(id, justification);
        return ResponseEntity.noContent().build();
    }
}
