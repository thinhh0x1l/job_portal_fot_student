package com.jobportal.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomAdminDetailsService implements UserDetailsService {
    private final AdminProperties adminProperties;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals(adminProperties.getUsername())) {
            return User.builder()
                    .username(adminProperties.getUsername())
                    .password(adminProperties.getPassword())
                    .roles(adminProperties.getRole())
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}
