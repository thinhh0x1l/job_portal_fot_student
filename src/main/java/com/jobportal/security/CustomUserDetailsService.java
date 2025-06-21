package com.jobportal.security;

import com.jobportal.entity.User;
import com.jobportal.model.Role;
import com.jobportal.repostory.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final HttpServletRequest request;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (request.getRequestURI().contains("recruiter")) {
            return userRepository.findByEmailAndRole(email, Role.RECRUITER)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
        } else if (request.getRequestURI().contains("lecturer")) {
            return userRepository.findByEmailAndRole(email,Role.LECTURER)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
        }
        return userRepository.findByEmailAndRole(email,Role.INTERN)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
    }
}
