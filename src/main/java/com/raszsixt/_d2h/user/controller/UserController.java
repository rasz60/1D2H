package com.raszsixt._d2h.user.controller;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.dto.SignupDto;
import com.raszsixt._d2h.user.dto.UserDto;
import com.raszsixt._d2h.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
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
    // 중복확인 (아이디, 메일, 연락처)
    @PostMapping("/dupChk")
    public ResponseEntity<?> dupChk(@RequestBody SignupDto signupDto) {
        return ResponseEntity.ok(userService.dupChk(signupDto));
    }
    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        return ResponseEntity.ok(userService.signup(signupDto));
    }
    // 로그인 & JWT 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto, request);
        return ResponseEntity.ok(loginResponseDto);
    }
    // RefreshToken이 유효하면 AccessToken 재발행
    @GetMapping("/check")
    public ResponseEntity<?> authCheck(HttpServletRequest request) {
        LoginResponseDto loginResponseDto = userService.authCheck(request);
        return ResponseEntity.ok(loginResponseDto);
    }
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("done");
    }
    // ID, PW 일치 여부 확인
    @PostMapping("/infoChk")
    public ResponseEntity<?> infoChk(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        UserDto res = userService.infoChk(loginRequestDto, request);
        HttpHeaders headers = new HttpHeaders();
        if (! res.getNewAccessToken().isEmpty() ) {
            headers.add("new-access-token", res.getNewAccessToken());
            res.setNewAccessToken("");
            return ResponseEntity.ok().headers(headers).body(res);
        }
        return ResponseEntity.ok(res);
    }
    // 회원정보 변경
    @PostMapping("/setUser")
    public ResponseEntity<?> setUser(@RequestBody UserDto userDto, HttpServletRequest request) {
        String res = userService.setUser(userDto, request);
        return ResponseEntity.ok(res);
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
