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
        if (user == null) throw new UsernameNotFoundException(loginIdentifier);

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
        String email = "", phoneNumber = "";

        if (identifier.contains(":")) {
            String[] cridentialsArray = identifier.split(":", -1); // preserve empty parts

            if (cridentialsArray.length == 2) {
                email = cridentialsArray[0].trim();
                phoneNumber = cridentialsArray[1].trim();

                if (!email.isEmpty() && !phoneNumber.isEmpty()) {
                    return userRepository.findByEmailAndPhoneNumber(email, phoneNumber)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with email and phone number combination"));
                } else if (!email.isEmpty()) {
                    return userRepository.findByEmail(email)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with that email"));
                } else if (!phoneNumber.isEmpty()) {
                    return userRepository.findByPhoneNumber(phoneNumber)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with that phone"));
                }
            }
        }

        throw new UsernameNotFoundException("Invalid login identifier format: " + identifier);
    }
}