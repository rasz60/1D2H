package com.raszsixt._d2h.user.controller;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> user) {
        String userId = user.get("userId");
        String password = passwordEncoder.encode(user.get("password"));
        String userEmail = user.get("userEmail");
        String userPhone = user.get("userPhone");

        // 필수 값 체크
        if ( Strings.isEmpty(userId) || Strings.isEmpty(password) || Strings.isEmpty(userEmail) || Strings.isEmpty(userPhone) ) {
            return ResponseEntity.badRequest().body("필수 값 (아이디, 비밀번호, 이메일, 전화번호)를 입력해주세요.");
        }

        // 중복 체크
        if ( userRepository.findByUserId(userId).isPresent() ) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }

        // 신규 User 저장
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserPwd(password);
        newUser.setUserEmail(userEmail);
        newUser.setUserPhone(userPhone);
        userRepository.save(newUser);

        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 & JWT 발급
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPwd())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user.getUserId());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
