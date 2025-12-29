package com.forumhub.security;

import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityValidationService {

    private final UserRepository userRepository;

    public User getCurrentAuthenticatedUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof User user) {
            email = user.getEmail();
        } else {
            email = principal.toString();
        }

        var userDetails = userRepository.findByEmail(email);
        if (userDetails == null) {
            throw new AccessDeniedException("User not found or not authenticated");
        }
        return (User) userDetails;
    }

    public void validateOwnerOrModerator(User resourceOwner) {
        var currentUser = getCurrentAuthenticatedUser();
        
        boolean isModerator = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MODERATOR"));
        
        if (!isModerator && !resourceOwner.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission denied");
        }
    }

    public boolean hasRole(String role) {
        var currentUser = getCurrentAuthenticatedUser();
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    public boolean isResourceOwner(Long userId) {
        var currentUser = getCurrentAuthenticatedUser();
        return currentUser.getId().equals(userId);
    }
}
