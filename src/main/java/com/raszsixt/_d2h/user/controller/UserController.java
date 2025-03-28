package com.raszsixt._d2h.user.controller;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.security.entity.RefreshToken;
import com.raszsixt._d2h.security.repository.RefreshTokenRepository;
import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import com.raszsixt._d2h.user.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> signupInfo) {
        try {
            String msg = userService.signup(signupInfo);
            return ResponseEntity.ok(msg);
        } catch ( IllegalArgumentException ie ) {
            return ResponseEntity.badRequest().body(ie.getMessage());
        }
    }
    // 로그인 & JWT 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
            return ResponseEntity.ok(loginResponseDto);
        } catch (AuthenticationException ae) {
            return ResponseEntity.badRequest().body(ae.getMessage());
        }
    }
    // RefreshToken이 유효하면 AccessToken 재발행
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        try {
            LoginResponseDto loginResponseDto = userService.refreshToken(refreshToken);
            return ResponseEntity.ok(loginResponseDto);
        } catch (RuntimeException re) {
            return ResponseEntity.badRequest().body(re.getMessage());
        }
    }
}
