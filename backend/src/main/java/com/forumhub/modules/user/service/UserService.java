package com.forumhub.modules.user.service;

import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> listAll() {
        log.debug("Listing all users");
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserResponseDTO updateProfile(@NonNull User user, @NonNull UserUpdateDTO updateRequest) {
        log.info("Updating profile for user ID: {}", user.getId());
        var existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with ID: " + user.getId()));
        
        existingUser.setName(updateRequest.name());
        existingUser.setEmail(updateRequest.email());
        
        if (updateRequest.password() != null && !updateRequest.password().isBlank()) {
            log.debug("Updating password for user ID: {}", user.getId());
            existingUser.setPassword(passwordEncoder.encode(updateRequest.password()));
        }
        
        return toDTO(existingUser);
    }

    @Transactional
    public void delete(@NonNull Long userId) {
        log.info("Deleting user ID: {}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public UserResponseDTO changeRole(@NonNull Long userId, @NonNull Long profileId) {
        log.info("Changing role for user ID: {} to profile ID: {}", userId, profileId);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with ID: " + userId));
        
        var profile = new Profile();
        profile.setId(profileId);
        user.setProfile(profile);
        
        var savedUser = userRepository.save(user);
        log.info("Role changed successfully for user ID: {}", userId);
        
        return toDTO(savedUser);
    }

    @Transactional
    public void block(@NonNull Long userId, @NonNull com.forumhub.modules.user.controller.UserController.BlockRequestDTO blockRequest, String justification) {
        log.info("Blocking user ID: {} with justification: {}", userId, justification);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with ID: " + userId));
        
        user.setSuspensionJustification(justification);
        if (Boolean.TRUE.equals(blockRequest.permanent())) {
            log.info("Applying permanent block to user ID: {}", userId);
            user.setIsPermanentBlocked(true);
        } else if (blockRequest.days() != null) {
            log.info("Applying temporary block of {} days to user ID: {}", blockRequest.days(), userId);
            user.setBlockedUntil(java.time.LocalDateTime.now().plusDays(blockRequest.days()));
        }
        
        userRepository.save(user);
    }

    @Transactional
    public void unblock(@NonNull Long userId) {
        log.info("Unblocking user ID: {}", userId);
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found with ID: " + userId));
        
        user.setIsPermanentBlocked(false);
        user.setBlockedUntil(null);
        userRepository.save(user);
        log.info("User ID: {} unblocked successfully", userId);
    }

    public UserResponseDTO toDTO(@NonNull User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getName() : "UNDEFINED",
                user.getCreatedAt()
        );
    }
}
