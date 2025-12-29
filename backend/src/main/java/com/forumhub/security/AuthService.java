package com.forumhub.security;

import com.forumhub.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Searching user in database by email: {}", username);
        var user = repository.findByEmail(username);
        if (user == null) {
            log.warn("User not found for email: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
