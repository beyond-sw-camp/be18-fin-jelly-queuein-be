package com.beyond.qiin.security.service;

import com.beyond.qiin.domain.iam.entity.User;
import com.beyond.qiin.domain.iam.repository.UserJpaRepository;
import com.beyond.qiin.domain.iam.repository.UserRoleJpaRepository;
import com.beyond.qiin.security.model.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;
    private final UserRoleJpaRepository userRoleJpaRepository; // 또는 UserRoleReader

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        final User user =
                userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        final String role = userRoleJpaRepository
                .findTopByUser_Id(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("역할이 존재하지 않습니다."))
                .getRole()
                .getRoleName();

        return new CustomUserDetails(
                user.getId(), user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
