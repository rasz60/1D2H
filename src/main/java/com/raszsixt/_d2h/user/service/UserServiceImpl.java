package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.security.entity.RefreshToken;
import com.raszsixt._d2h.security.repository.RefreshTokenRepository;
import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.dto.SignupDto;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
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
    // 회원가입
    @Override
    public String signup(SignupDto signupDto) throws IllegalArgumentException {
        // 필수 값 체크
        String userId = signupDto.getSignupUserId();
        String userPwd = signupDto.getSignupUserPwd();
        String userEmail = signupDto.getUserEmailId() + "@" + signupDto.getUserEmailDomain();
        String userPhone = signupDto.getUserPhone();
        if (Strings.isEmpty(userId) || Strings.isEmpty(userPwd) || Strings.isEmpty(userEmail) || Strings.isEmpty(userPhone)) {
            throw new IllegalArgumentException("필수 값 (아이디, 비밀번호, 이메일, 전화번호)를 입력해주세요.");
        }
        
        // 중복 확인
        SignupDto dupChkDto = new SignupDto();
        dupChkDto.setSignupUserId(signupDto.getSignupUserId());
        dupChkDto.setUserEmailId(signupDto.getUserEmailId());
        dupChkDto.setUserEmailDomain(signupDto.getUserEmailDomain());
        dupChkDto.setUserPhone(signupDto.getUserPhone());
        dupChkDto = dupChk(dupChkDto);

        String errMsg = "";
        if ( dupChkDto.isSignupUserIdDupChk() ) {
            errMsg = "이미 사용 중인 아이디 입니다.";
        } else if ( dupChkDto.isUserEmailDupChk() ) {
            errMsg = "이미 가입된 이메일 주소 입니다.";
        } else if ( dupChkDto.isUserPhoneDupChk() ) {
            errMsg = "이미 가입된 연락처 입니다.";
        }

        if (! Strings.isEmpty(errMsg) ) {
            throw new IllegalArgumentException(errMsg);
        }

        // 신규 User 저장
        User newUser = User.of(signupDto);
        newUser.setUserPwd(passwordEncoder.encode(signupDto.getSignupUserPwd()));
        newUser.setUserRole("ROLE_USER");
        userRepository.save(newUser);

        return "회원 가입에 성공했습니다.";
    }
    // 회원 가입 중복 확인
    @Override
    public SignupDto dupChk(SignupDto signupDto) {
        // id 중복 확인
        boolean flag = userRepository.findByUserId(signupDto.getSignupUserId()).isPresent();
        signupDto.setSignupUserIdDupChk(flag);

        if (! flag ) {
            // email 중복 확인
            signupDto.setUserEmail(signupDto.getUserEmailId() + "@" + signupDto.getUserEmailDomain());
            flag = userRepository.findByUserEmail(signupDto.getUserEmail()).isPresent();
            signupDto.setUserEmailDupChk(flag);
        }

        if (! flag ) {
            // 연락처 중복 확인
            flag = userRepository.findByUserPhone(signupDto.getUserPhone()).isPresent();
            signupDto.setUserPhoneDupChk(flag);
        }
        return signupDto;
    }
    // 로그인
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserId(), loginRequestDto.getUserPwd())
        );
        // Principal에서 User 객체 가져오기
        User user = (User) authentication.getPrincipal();
        String userId = user.getUserId();
        String accessToken = jwtUtil.generateAccessToken(userId); // accesstoken 생성

        // 최초 접속 시, 최초 접속일 (firstVisitDate) 오늘 날짜로 설정
        if ( user.getFirstVisitDate() == null ) {
            userRepository.updateFirstVisitDate(user.getUserMgmtNo(), LocalDateTime.now());
        }
        // 마지막 접속일 (latestVisitDate) 오늘 날짜로 설정
        userRepository.updateLatestVisitDate(user.getUserMgmtNo(), LocalDateTime.now());

        // 같은 아이디의 refreshToken이 존재하면 삭제하고 저장
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        String device = request.getHeader("X-device-info");
        String deviceInfo = device + " - " + ip;

        List<RefreshToken> exists = refreshTokenRepository.findByUserId(userId);
        exists.forEach(refreshTokenRepository::delete);
    
        // refreshToken 생성 및 저장
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(userId, refreshToken, deviceInfo));

        return new LoginResponseDto(accessToken);
    }
    // token refresh 처리
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
    // 로그아웃
    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtUtil.resolveAccessToken(request); // request에서 token 추출
        String userId = jwtUtil.getUserIdFromToken(token); // token으로 id 추출
        if (!Strings.isEmpty(userId)) {
            // header에 device info 추출
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null) {
                ip = request.getRemoteAddr();
            }
            String device = request.getHeader("X-device-info");
            String deviceInfo = device + " - " + ip;

            // 아이디의 refreshToken 삭제
            Optional<RefreshToken> exists = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);
            exists.ifPresent(refreshTokenRepository::delete);
        }
    }
    // 로그인 유저의 role 조회
    @Override
    public String getLoginUserRole(String userId) {
        return userRepository.findByUserId(userId).get().getUserRole();
    }
}
