package com.forumhub.modules.auth.controller;

import com.forumhub.modules.auth.dto.LoginRequestDTO;
import com.forumhub.modules.auth.dto.TokenResponseDTO;
import com.forumhub.modules.user.dto.UserResponseDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.ProfileRepository;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.modules.user.service.UserService;
import com.forumhub.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and Registration")
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        var authentication = manager.authenticate(authenticationToken);
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegistrationRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // ROLE_USER fixed UUID
        UUID userProfileId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        Profile userProfile = profileRepository.findById(userProfileId)
                .orElseGet(() -> profileRepository.save(new Profile(userProfileId, "ROLE_USER")));

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .profile(userProfile)
                .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.toDTO(user));
    }

    public record RegistrationRequest(String name, String email, String password) {}
}
