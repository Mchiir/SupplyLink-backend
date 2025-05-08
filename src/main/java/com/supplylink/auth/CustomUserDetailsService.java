package com.supplylink.auth;

import com.supplylink.models.User;
import com.supplylink.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        User user = resolveUserByIdentifier(loginIdentifier);

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                loginIdentifier,
                user.getPassword(),
                authorities
        );
    }

    private User resolveUserByIdentifier(String identifier) {
        if (identifier.contains(":")) {
            String[] parts = identifier.split(":");
            if (parts.length != 2) {
                throw new UsernameNotFoundException("Invalid login identifier format: expected email:phone");
            }

            String email = parts[0].trim();
            String phone = parts[1].trim();

            return userRepository.findByEmailAndPhoneNumber(email, phone)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found with email and phone number combination"));
        } else {
            return userRepository.findByEmail(identifier)
                    .or(() -> userRepository.findByPhoneNumber(identifier))
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found with email or phone: " + identifier));
        }
    }
}