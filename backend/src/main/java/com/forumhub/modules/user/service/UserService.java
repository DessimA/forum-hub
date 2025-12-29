package com.forumhub.modules.user.service;

import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.dto.UserUpdateDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> listAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserResponseDTO updateProfile(@NonNull User user, @NonNull UserUpdateDTO dto) {
        var existingUser = repository.findById(user.getId()).orElseThrow();
        existingUser.setName(dto.name());
        existingUser.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.password()));
        }
        return toDTO(existingUser);
    }

    @Transactional
    public void delete(@NonNull Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO changeRole(@NonNull Long userId, @NonNull Long profileId) {
        var user = repository.findById(userId).orElseThrow();
        var profile = new Profile();
        profile.setId(profileId);
        user.setProfile(profile);
        return toDTO(repository.save(user));
    }

    @Transactional
    public void block(@NonNull Long userId, @NonNull com.forumhub.modules.user.controller.UserController.BlockRequestDTO dto, String justification) {
        var user = repository.findById(userId).orElseThrow();
        user.setSuspensionJustification(justification);
        if (Boolean.TRUE.equals(dto.permanent())) {
            user.setIsPermanentBlocked(true);
        } else if (dto.days() != null) {
            user.setBlockedUntil(java.time.LocalDateTime.now().plusDays(dto.days()));
        }
        repository.save(user);
    }

    @Transactional
    public void unblock(@NonNull Long userId) {
        var user = repository.findById(userId).orElseThrow();
        user.setIsPermanentBlocked(false);
        user.setBlockedUntil(null);
        repository.save(user);
    }

    public UserResponseDTO toDTO(@NonNull User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile().getName(),
                user.getCreatedAt()
        );
    }
}