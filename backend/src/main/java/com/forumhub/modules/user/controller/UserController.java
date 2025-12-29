package com.forumhub.modules.user.controller;

import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Users", description = "User profile and management")
public class UserController {
    private final UserService service;
    private final com.forumhub.modules.user.repository.UserRepository userRepository;

    private User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof User user) {
            email = user.getEmail();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile data")
    public ResponseEntity<UserResponseDTO> getProfile() {
        var user = getCurrentUser();
        return ResponseEntity.ok(service.toDTO(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        var user = getCurrentUser();
        return ResponseEntity.ok(service.updateProfile(user, dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users (Admin only)")
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a user (Admin only)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user role (Admin only)")
    public ResponseEntity<UserResponseDTO> changeRole(@PathVariable Long id, @RequestBody Long profileId) {
        var currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.changeRole(id, profileId));
    }

    @PostMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Block user (Admin/Moderator only)")
    public ResponseEntity<Void> blockUser(@PathVariable Long id, @RequestBody BlockRequestDTO dto) {
        var currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        
        // Moderador só pode suspender (não banir permanente) e por no máximo 1 dia
        boolean isOnlyModerator = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"))
                && currentUser.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isOnlyModerator) {
            if (Boolean.TRUE.equals(dto.permanent()) || (dto.days() != null && dto.days() > 1)) {
                return ResponseEntity.status(403).build();
            }
        }

        service.block(id, dto, dto.justification());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock user (Admin only)")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        service.unblock(id);
        return ResponseEntity.ok().build();
    }

    public record BlockRequestDTO(Boolean permanent, Integer days, String justification) {}
}
