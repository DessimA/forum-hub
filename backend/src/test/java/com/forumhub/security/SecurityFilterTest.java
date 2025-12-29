package com.forumhub.security;

import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate when token is valid")
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(tokenService.getSubject("valid_token")).thenReturn("user@example.com");
        
        Profile profile = new Profile(1L, "ROLE_USER");
        User user = User.builder().id(1L).email("user@example.com").profile(profile).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should throw exception when token is invalid")
    void doFilterInternal_WithInvalidToken_ShouldThrowException() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(tokenService.getSubject("invalid_token")).thenThrow(new RuntimeException("Invalid Token"));

        assertThatThrownBy(() -> securityFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid Token");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should continue without auth when token is missing")
    void doFilterInternal_WithoutToken_ShouldContinue() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
