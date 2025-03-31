package com.raszsixt._d2h.user.controller;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        String msg = userService.signup(signupInfo);
        return ResponseEntity.ok(msg);
    }
    // 로그인 & JWT 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }
    // RefreshToken이 유효하면 AccessToken 재발행
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        LoginResponseDto loginResponseDto = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(loginResponseDto);
    }
    // ADMIN_ONLY
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnly() {
        return ResponseEntity.ok("이 API는 ADMIN만 접근할 수 있습니다.");
    }
    // ADMIN_ONLY
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/user-or-admin")
    public ResponseEntity<?> userOrAdmin() {
        return ResponseEntity.ok("이 API는 USER 또는 ADMIN만 접근할 수 있습니다.");
    }
}
