package com.tanduydev.ecommerce.config;

import com.tanduydev.ecommerce.model.Role;
import com.tanduydev.ecommerce.model.User;
import com.tanduydev.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        Role role = user.getRole();
        if (role == null) {
            throw new RuntimeException("User chưa được cấp quyền (Role).");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // Mật khẩu đã mã hóa Bcrypt
                .authorities(Collections.singletonList(authority))
                .build();
    }
}