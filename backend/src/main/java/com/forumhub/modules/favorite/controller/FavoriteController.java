package com.forumhub.modules.favorite.controller;

import com.forumhub.modules.favorite.service.FavoriteService;
import com.forumhub.modules.topic.dto.TopicResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing user favorite topics")
@SecurityRequirement(name = "bearer-key")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/topics/{topicId}")
    @Operation(summary = "Add topic to favorites", description = "Adds a specific topic to the current user's favorites list")
    public ResponseEntity<Void> addFavorite(@PathVariable UUID topicId) {
        favoriteService.addFavorite(topicId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/topics/{topicId}")
    @Operation(summary = "Remove topic from favorites", description = "Removes a specific topic from the current user's favorites list")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID topicId) {
        favoriteService.removeFavorite(topicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List favorite topics", description = "Retrieves a paginated list of all topics favorited by the current user")
    public ResponseEntity<Page<TopicResponseDTO>> listUserFavorites(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(favoriteService.listUserFavorites(pageable));
    }

    @GetMapping("/topics/{topicId}/check")
    @Operation(summary = "Check if topic is favorited", description = "Checks if the current user has favorited a specific topic")
    public ResponseEntity<Boolean> isFavorited(@PathVariable UUID topicId) {
        return ResponseEntity.ok(favoriteService.isFavorited(topicId));
    }
}