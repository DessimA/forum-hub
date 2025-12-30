package com.forumhub.security;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityValidationServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private SecurityValidationService service;

    @Test
    @DisplayName("Should return current user")
    void shouldReturnCurrentUser() {
        String email = "user@test.com";
        UUID userId = UUID.randomUUID();
        Profile profile = new Profile(UUID.randomUUID(), "ROLE_USER");
        User user = User.builder().id(userId).email(email).profile(profile).build();
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        User result = service.getCurrentAuthenticatedUser();

        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should throw exception when user is blocked")
    void shouldThrowWhenBlocked() {
        String email = "blocked@test.com";
        Profile profile = new Profile(UUID.randomUUID(), "ROLE_USER");
        User user = User.builder().id(UUID.randomUUID()).email(email).profile(profile).isPermanentBlocked(true).build();
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));

        assertThatThrownBy(() -> service.getCurrentAuthenticatedUser())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("suspended or banned");
    }
}
