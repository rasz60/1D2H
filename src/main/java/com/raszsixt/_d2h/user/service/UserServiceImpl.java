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
        newUser.setUserSignOutYn("N");
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

        return new LoginResponseDto(accessToken, this.getAuthLevel(user.getUserRole()));
    }
    // token refresh 처리
    @Override
    public LoginResponseDto authCheck(HttpServletRequest request) throws AuthenticationException {
        String token = null;
        String refreshToken = null;
        String userId = null;
        String role = "guest";

        // 1. request에서 Access Token 추출
        token = jwtUtil.resolveAccessToken(request);

        // 2. request Access Token 존재하지만, 유효하지 않을 때
        if ( token != null && ! jwtUtil.validateToken(token) ) {
            // 2-1. token 초기화
            token = null;
        }

        // 3. request에 Access Token이 없는 경우
        if ( token == null ) {
            // 3-1. Refresh Token 조회하여 새로운 Access Token 발급
            token = jwtUtil.resolveNewAccessToken(request);
        }

        // 4. token에서 추출한 userId에 userRole 추가
        if ( token != null ) {
            userId = jwtUtil.getUserIdFromToken(token);
            role = getLoginUserRole(userId);
        }

        return new LoginResponseDto(token, this.getAuthLevel(role));
    }
    // 로그아웃
    @Override
    public void logout(HttpServletRequest request) {
        String token = null;
        String refreshToken = null;
        String userId = null;

        // 1. request에서 access token 추출
        token = jwtUtil.resolveAccessToken(request);

        // 2. token 이 없을 때
        if ( token == null ) {
            refreshToken = jwtUtil.resolveRefreshToken(request);
        }

        // 3. 어떠한 token을 찾을 수 없을 때, 바로 return
        if ( token == null && refreshToken == null ) {
            return;
        }

        // 4. refreshToken 이 있을 때, 바로 삭제
        if ( refreshToken != null ) {
            refreshTokenRepository.findByRefreshToken(refreshToken).ifPresent(refreshTokenRepository::delete);
        }
        // 5. refreshToken 이 없을 때, userId와 deviceInfo로 찾아낸 refreshToken 삭제
        else {
            userId = jwtUtil.getUserIdFromToken(token); // token으로 id 추출
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
    }
    // 로그인 유저의 role 조회
    @Override
    public String getLoginUserRole(String userId) {
        return userRepository.findByUserId(userId).get().getUserRole();
    }

    public int getAuthLevel(String userRole) {
        int auth = 0;

        if ( "guest".equals(userRole) ) {
            auth = 1;
        } else if ( "ROLE_USER".equals(userRole) ) {
            auth = 2;
        } else if ( "ROLE_ADMIN".equals(userRole) ) {
            auth = 3;
        }
        return auth;
    }
}
