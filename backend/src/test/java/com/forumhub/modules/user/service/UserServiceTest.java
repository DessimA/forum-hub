package com.forumhub.modules.user.service;

import com.forumhub.modules.user.controller.UserController;
import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile(1L, "ROLE_USER");
        user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("old@example.com")
                .password("encoded_old_password")
                .profile(profile)
                .build();
    }

    @Test
    @DisplayName("Should update profile with valid data")
    void updateProfile_WithValidData_ShouldUpdate() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("New Name", "new@example.com", null);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.updateProfile(user, updateDTO);

        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded_old_password"); // Not changed
    }

    @Test
    @DisplayName("Should update profile with password")
    void updateProfile_WithPassword_ShouldEncryptAndUpdate() {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Name", "email@example.com", "new_password");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        userService.updateProfile(user, updateDTO);

        assertThat(user.getPassword()).isEqualTo("encoded_new_password");
    }

    @Test
    @DisplayName("Should change role")
    void changeRole_ShouldUpdateProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.changeRole(1L, 2L);

        assertThat(user.getProfile().getId()).isEqualTo(2L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should block user permanently")
    void block_Permanent_ShouldSetFlag() {
        UserController.BlockRequestDTO blockDTO = new UserController.BlockRequestDTO(true, null, "Spam");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.block(1L, blockDTO, "Spam");

        assertThat(user.getIsPermanentBlocked()).isTrue();
        assertThat(user.getSuspensionJustification()).isEqualTo("Spam");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should block user temporarily")
    void block_Temporary_ShouldSetDate() {
        UserController.BlockRequestDTO blockDTO = new UserController.BlockRequestDTO(false, 7, "Violation");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.block(1L, blockDTO, "Violation");

        assertThat(user.getIsPermanentBlocked()).isFalse();
        assertThat(user.getBlockedUntil()).isAfter(LocalDateTime.now());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should unblock user")
    void unblock_ShouldClearFlags() {
        user.setIsPermanentBlocked(true);
        user.setBlockedUntil(LocalDateTime.now().plusDays(1));
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.unblock(1L);

        assertThat(user.getIsPermanentBlocked()).isFalse();
        assertThat(user.getBlockedUntil()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should delete user")
    void delete_ShouldRemoveUser() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}
