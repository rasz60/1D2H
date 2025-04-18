package com.raszsixt._d2h.security.service;

import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // USERID 존재 여부 확인 후 userDetails 객체로 return
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUserIdAndUserSignOutYn(username, "N");
        if ( userOptional.isEmpty() ) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }

        return userOptional.get();
    }
}
