package com.forumhub.modules.user.service;

import com.forumhub.modules.user.controller.UserController;
import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.audit.service.AuditLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final com.forumhub.modules.topic.service.TopicService topicService;
    private final com.forumhub.modules.answer.service.AnswerService answerService;

    @Transactional
    public UserResponseDTO updateProfile(User user, UserUpdateDTO dto) {
        log.info("Updating profile for user: {}", user.getEmail());
        
        var entity = userRepository.findById(user.getId()).orElseThrow();
        
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.email() != null) entity.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.password()));
        }
        
        return toDTO(entity);
    }

    public List<UserResponseDTO> listAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting user ID: {}", id);
        auditLogService.logAction("DELETE", "USER", id, "User deleted", "Cleanup");
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO changeRole(UUID id, UUID profileId) {
        log.info("Changing role for user ID: {} to profile ID: {}", id, profileId);
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        var profile = new com.forumhub.modules.user.model.Profile();
        profile.setId(profileId);
        user.setProfile(profile);
        
        auditLogService.logAction("UPDATE_ROLE", "USER", id, "New profile: " + profileId, "Admin action");
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void block(UUID id, UserController.BlockRequestDTO dto, String justification) {
        log.info("Blocking user ID: {}. Permanent: {}", id, dto.permanent());
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        user.setSuspensionJustification(justification);
        
        if (Boolean.TRUE.equals(dto.permanent())) {
            user.setIsPermanentBlocked(true);
            user.setBlockedUntil(null);
            auditLogService.logAction("PERMANENT_BLOCK", "USER", id, justification, justification);
        } else {
            user.setIsPermanentBlocked(false);
            user.setBlockedUntil(LocalDateTime.now().plusDays(dto.days()));
            auditLogService.logAction("TEMPORARY_BLOCK", "USER", id, "Days: " + dto.days(), justification);
        }
        userRepository.save(user);

        // Automatic content cleanup based on context
        if (dto.topicId() != null) {
            log.info("Automatically deleting topic {} due to author punishment", dto.topicId());
            topicService.delete(dto.topicId(), "Author punished: " + justification);
        }
        
        if (dto.answerId() != null) {
            log.info("Automatically deleting answer {} due to author punishment", dto.answerId());
            answerService.delete(dto.answerId(), "Author punished: " + justification);
        }
    }

    @Transactional
    public void unblock(UUID id) {
        log.info("Unblocking user ID: {}", id);
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setIsPermanentBlocked(false);
        user.setBlockedUntil(null);
        user.setSuspensionJustification(null);
        userRepository.save(user);
        auditLogService.logAction("UNBLOCK", "USER", id, "User unblocked", "Admin action");
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getName() : "ROLE_USER",
                user.getCreatedAt(),
                user.getIsPermanentBlocked(),
                user.getBlockedUntil()
        );
    }
}
