package com.forumhub.security;

import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityValidationService {

    private final UserRepository userRepository;

    public User getCurrentAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        
        var principal = auth.getPrincipal();
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof User user) {
            email = user.getEmail();
        } else {
            email = principal.toString();
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));
        
        // Defesa em profundidade: Verifica se o usuário está bloqueado
        if (Boolean.TRUE.equals(user.getIsPermanentBlocked()) || 
           (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(LocalDateTime.now()))) {
            throw new AccessDeniedException("Account is suspended or banned");
        }
        
        return user;
    }

    public void validateOwnerOrModerator(User resourceOwner) {
        var currentUser = getCurrentAuthenticatedUser();
        
        boolean isModerator = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MODERATOR"));
        
        if (!isModerator && !resourceOwner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }
    }

    public void validateOwner(User resourceOwner) {
        var currentUser = getCurrentAuthenticatedUser();
        if (!resourceOwner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied: You must be the owner to perform this action");
        }
    }

    public boolean hasRole(String role) {
        var currentUser = getCurrentAuthenticatedUser();
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    public boolean isResourceOwner(UUID userId) {
        var currentUser = getCurrentAuthenticatedUser();
        return currentUser.getId().equals(userId);
    }
}
