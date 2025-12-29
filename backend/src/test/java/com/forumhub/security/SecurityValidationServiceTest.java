package com.forumhub.security;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityValidationServiceTest {

    @InjectMocks
    private SecurityValidationService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User currentUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        
        Profile userProfile = new Profile(1L, "ROLE_USER");
        currentUser = User.builder().id(1L).email("user@example.com").profile(userProfile).build();
        
        Profile otherProfile = new Profile(2L, "ROLE_USER");
        otherUser = User.builder().id(2L).email("other@example.com").profile(otherProfile).build();
    }

    private void mockAuthentication(User user) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(user);
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
    }

    @Test
    @DisplayName("Should return current user")
    void getCurrentAuthenticatedUser_ShouldReturnUser() {
        mockAuthentication(currentUser);
        User result = service.getCurrentAuthenticatedUser();
        assertThat(result).isEqualTo(currentUser);
    }

    @Test
    @DisplayName("validateOwnerOrModerator should pass for owner")
    void validateOwnerOrModerator_AsOwner_ShouldPass() {
        mockAuthentication(currentUser);
        service.validateOwnerOrModerator(currentUser);
    }

    @Test
    @DisplayName("validateOwnerOrModerator should pass for moderator")
    void validateOwnerOrModerator_AsModerator_ShouldPass() {
        Profile modProfile = new Profile(3L, "ROLE_MODERATOR");
        User modUser = User.builder().id(3L).email("mod@example.com").profile(modProfile).build();
        mockAuthentication(modUser);

        service.validateOwnerOrModerator(otherUser);
    }

    @Test
    @DisplayName("validateOwnerOrModerator should throw for unauthorized user")
    void validateOwnerOrModerator_AsUnauthorized_ShouldThrow() {
        mockAuthentication(currentUser);
        
        assertThatThrownBy(() -> service.validateOwnerOrModerator(otherUser))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Permission denied");
    }
}
