package com.forumhub.modules.auth.controller;

import com.forumhub.modules.auth.dto.LoginRequestDTO;
import com.forumhub.modules.auth.dto.RegisterRequestDTO;
import com.forumhub.modules.auth.dto.TokenResponseDTO;
import com.forumhub.modules.user.model.Profile;
import com.forumhub.modules.user.model.User;
import com.forumhub.modules.user.repository.UserRepository;
import com.forumhub.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login and return JWT token")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var authentication = manager.authenticate(authenticationToken);
        var tokenJWT = tokenService.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var profile = new Profile(3L, "ROLE_USER");
        var user = User.builder()
                .name(dto.nome())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.senha()))
                .profile(profile)
                .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}