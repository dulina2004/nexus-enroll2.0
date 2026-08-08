package com.nexusenroll.auth.security;

import com.nexusenroll.auth.model.User;
import com.nexusenroll.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));

        String roleName = "ROLE_" + (user.getRole() != null ? user.getRole().name() : "STUDENT");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                "ACTIVE".equalsIgnoreCase(user.getStatus()),
                true,
                true,
                user.getLockedUntil() == null || user.getLockedUntil().isBefore(java.time.Instant.now()),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
