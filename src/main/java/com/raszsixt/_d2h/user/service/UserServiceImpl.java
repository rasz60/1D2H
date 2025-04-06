package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.security.entity.RefreshToken;
import com.raszsixt._d2h.security.repository.RefreshTokenRepository;
import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String signup(Map<String, String> signupInfo) throws IllegalArgumentException {
        String userId = signupInfo.get("userId");
        String userPwd = signupInfo.get("userPwd");
        String userEmail = signupInfo.get("userEmail");
        String userPhone = signupInfo.get("userPhone");

        // 필수 값 체크
        if (Strings.isEmpty(userId) || Strings.isEmpty(userPwd) || Strings.isEmpty(userEmail) || Strings.isEmpty(userPhone)) {
            throw new IllegalArgumentException("필수 값 (아이디, 비밀번호, 이메일, 전화번호)를 입력해주세요.");
        }

        // 중복 체크
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 신규 User 저장
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUserPwd(passwordEncoder.encode(userPwd));
        newUser.setUserEmail(userEmail);
        newUser.setUserPhone(userPhone);
        newUser.setUserRole("ROLE_USER");

        userRepository.save(newUser);

        return "회원가입 성공";
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserId(), loginRequestDto.getUserPwd())
        );

        User user = (User) authentication.getPrincipal();
        String userId = user.getUserId();
        String accessToken = jwtUtil.generateAccessToken(userId);
        String deviceInfo = request.getHeader("X-device-info");

        // 같은 아이디의 refreshToken이 존재하면 삭제하고 저장
        Optional<RefreshToken> exists = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);
        exists.ifPresent(refreshTokenRepository::delete);

        String refreshToken = jwtUtil.generateRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(userId, refreshToken, deviceInfo));

        return new LoginResponseDto(accessToken);
    }

    @Override
    public LoginResponseDto refreshToken(String refreshToken) throws RuntimeException {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);

        // 존재하지 않는 token 일 때
        if (optionalRefreshToken.isEmpty()) {
            throw new RuntimeException("존재하지 않는 RefreshToken 입니다.");
        }

        // 만료됐을 때
        if (!jwtUtil.validateToken(refreshToken)) {
            refreshTokenRepository.delete(optionalRefreshToken.get());
            throw new RuntimeException("만료된 RefreshToken 입니다.");
        }

        // 새로운 AccessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(jwtUtil.getUserIdFromToken(refreshToken));
        return new LoginResponseDto(newAccessToken);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        String userId = jwtUtil.getUserIdFromToken(token);

        if (!Strings.isEmpty(userId)) {
            String deviceInfo = request.getHeader("X-device-info");

            // 아이디의 refreshToken 삭제
            Optional<RefreshToken> exists = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);
            exists.ifPresent(refreshTokenRepository::delete);
        }
    }
}
