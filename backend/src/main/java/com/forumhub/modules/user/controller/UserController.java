package com.forumhub.modules.user.controller;

import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.service.UserService;
import com.forumhub.security.SecurityValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Users", description = "Endpoints for user profile management and administrative user control")
public class UserController {
    private final UserService service;
    private final SecurityValidationService securityService;

    @GetMapping("/profile")
    @Operation(summary = "Get current profile", description = "Retrieves the profile data of the currently authenticated user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
    public ResponseEntity<UserResponseDTO> getProfile() {
        var user = securityService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(service.toDTO(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current profile", description = "Updates the profile information of the currently authenticated user.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid profile data")
        })
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        var user = securityService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(service.updateProfile(user, dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users", description = "Retrieves a list of all registered users. Restricted to administrators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Users list retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required")
        })
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Permanently deletes a user account. Restricted to administrators. Users cannot delete themselves.",
        responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request: User cannot delete their own account"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required")
        })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Fixed: UUID check
        if (securityService.isResourceOwner(id)) {
            return ResponseEntity.badRequest().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user role", description = "Updates the profile/role of a user. Restricted to administrators. Users cannot change their own role.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request: User cannot change their own role"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Admin role required")
        })
    public ResponseEntity<UserResponseDTO> changeRole(@PathVariable UUID id, @RequestBody UUID profileId) {
        // Fixed: UUID check
        if (securityService.isResourceOwner(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.changeRole(id, profileId));
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Block user", description = "Temporarily or permanently blocks a user account. Restricted to administrators and moderators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User blocked successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient permissions to block this user"),
            @ApiResponse(responseCode = "404", description = "User not found")
        })
    public ResponseEntity<Void> blockUser(@PathVariable UUID id, @RequestBody @Valid BlockRequestDTO dto) {
        // Fixed: UUID check
        if (securityService.isResourceOwner(id)) {
            return ResponseEntity.badRequest().build();
        }

        var targetUser = service.findById(id);
        var targetUserDTO = service.toDTO(targetUser);
        boolean targetIsAdmin = "ROLE_ADMIN".equals(targetUserDTO.profile());
        boolean currentUserIsAdmin = securityService.hasRole("ROLE_ADMIN");

        if (targetIsAdmin && !currentUserIsAdmin) {
            return ResponseEntity.status(403).build();
        }
        
        boolean isOnlyModerator = securityService.hasRole("ROLE_MODERATOR") && !currentUserIsAdmin;
        
        if (isOnlyModerator) {
            if (Boolean.TRUE.equals(dto.permanent()) || (dto.days() != null && dto.days() > 1)) {
                return ResponseEntity.status(403).build();
            }
        }

        service.block(id, dto, dto.justification());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Unblock user", description = "Removes a block from a user account. Restricted to administrators and moderators.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User unblocked successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Insufficient permissions to unblock this user"),
            @ApiResponse(responseCode = "404", description = "User not found")
        })
    public ResponseEntity<Void> unblockUser(@PathVariable UUID id) {
        var targetUser = service.findById(id);
        var targetUserDTO = service.toDTO(targetUser);
        boolean targetIsAdmin = "ROLE_ADMIN".equals(targetUserDTO.profile());
        boolean currentUserIsAdmin = securityService.hasRole("ROLE_ADMIN");

        if (targetIsAdmin && !currentUserIsAdmin) {
            return ResponseEntity.status(403).build();
        }

        service.unblock(id);
        return ResponseEntity.ok().build();
    }

    @Schema(description = "Data for blocking a user account")
    public record BlockRequestDTO(
        @Schema(description = "Whether the block is permanent", example = "false")
        Boolean permanent,
        @Schema(description = "Number of days for temporary block", example = "7")
        Integer days,
        @Schema(description = "Reason for the block", example = "Offensive behavior")
        String justification,
        @Schema(description = "Related topic ID that triggered the block")
        UUID topicId,
        @Schema(description = "Related answer ID that triggered the block")
        UUID answerId
    ) {}
}
