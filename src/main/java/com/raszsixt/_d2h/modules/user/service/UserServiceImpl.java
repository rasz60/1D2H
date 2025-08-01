package com.raszsixt._d2h.modules.user.service;

import com.raszsixt._d2h.common.base.BaseDto;
import com.raszsixt._d2h.common.mail.dto.MailDto;
import com.raszsixt._d2h.common.mail.service.MailService;
import com.raszsixt._d2h.common.search.dto.SearchDto;
import com.raszsixt._d2h.common.search.specification.SearchSpecification;
import com.raszsixt._d2h.modules.user.dto.*;
import com.raszsixt._d2h.modules.user.entity.User;
import com.raszsixt._d2h.modules.user.repository.UserRepository;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.security.entity.RefreshToken;
import com.raszsixt._d2h.security.repository.RefreshTokenRepository;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final MailService mailService;
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
        DupChkDto dupChkDto = DupChkDto.of(signupDto);
        dupChkDto = dupChk(dupChkDto);

        String errMsg = "";
        if ( dupChkDto.isUserIdDupChk() ) {
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
    public DupChkDto dupChk(DupChkDto dupChkDto) {
        boolean flag = false;
        // singup일 때 id 중복 확인
        if ( "signup".equals(dupChkDto.getDupChkType()) ) {
            flag = userRepository.findByUserIdAndUserSignOutYn(dupChkDto.getUserId(), "N").isPresent();
            dupChkDto.setUserIdDupChk(flag);
        }

        if (! flag ) {
            // email 중복 확인
            dupChkDto.setUserEmail(dupChkDto.getUserEmail());
            flag = userRepository.findByUserEmailAndUserSignOutYnAndUserIdNot(dupChkDto.getUserEmail(), "N", dupChkDto.getUserId()).isPresent();
            dupChkDto.setUserEmailDupChk(flag);
        }

        if (! flag ) {
            // 연락처 중복 확인
            flag = userRepository.findByUserPhoneAndUserSignOutYnAndUserIdNot(dupChkDto.getUserPhone(), "N", dupChkDto.getUserId()).isPresent();
            dupChkDto.setUserPhoneDupChk(flag);
        }
        return dupChkDto;
    }
    // 로그인
    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserId(), loginRequestDto.getUserPwd())
        );
        // Principal에서 User 객체 가져오기
        User user = (User) authentication.getPrincipal();

        if ( "Y".equals(user.getUserSignOutYn()) ) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

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

        return new LoginResponseDto(accessToken, user.getUserId(),this.getAuthLevel(user.getUserRole()));
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

        return new LoginResponseDto(token, userId, this.getAuthLevel(role));
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

    @Override
    public UserDto infoChk(LoginRequestDto loginRequestDto, HttpServletRequest request) throws BadCredentialsException, SecurityException {
        String token = null;
        boolean isNewToken = false;
        String extractUserId = null;

        // 1. header에서 token 추출
        token = jwtUtil.resolveAccessToken(request);

        // 2. token이 null인 경우 refresh token으로 재발급 처리
        if ( token == null ) {
            token = jwtUtil.resolveNewAccessToken(request);
            isNewToken = token != null;
        }
        
        // 3. token에서 userId 추출
        if ( token != null && jwtUtil.validateToken(token) ) {
            extractUserId = jwtUtil.getUserIdFromToken(token);
        }

        // 4. userId와 요청 Id가 같은지 확인, 다르면 실패
        if ( token == null || extractUserId == null || ! extractUserId.equals(loginRequestDto.getUserId()) ) {
            throw new SecurityException("로그인 유효기간이 만료되었습니다.\n다시 로그인 해주시길 바랍니다.");
        }

        // 5. userId와 userPwd가 일치하는지 확인
        Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn(loginRequestDto.getUserId(),"N");
        if ( exsist.isEmpty() || !passwordEncoder.matches(loginRequestDto.getUserPwd(),exsist.get().getUserPwd()) ) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 6. return bind
        UserDto dto = UserDto.of(exsist.get());
        dto.setNewAccessToken(isNewToken ? token : "");
        return dto;
    }

    public String setUser(UserDto userDto, HttpServletRequest request)  throws IllegalArgumentException {
        // 필수 값 체크
        String userEmail = userDto.getUserEmailId() + "@" + userDto.getUserEmailDomain();
        String userPhone = userDto.getUserPhone();
        if (Strings.isEmpty(userEmail) || Strings.isEmpty(userPhone)) {
            throw new IllegalArgumentException("필수 값 (이메일, 전화번호)를 입력해주세요.");
        }

        // 중복 확인
        DupChkDto dupChkDto = DupChkDto.of(userDto);
        dupChkDto = dupChk(dupChkDto);

        String errMsg = "";
        if ( dupChkDto.isUserEmailDupChk() ) {
            errMsg = "이미 가입된 이메일 주소 입니다.";
        } else if ( dupChkDto.isUserPhoneDupChk() ) {
            errMsg = "이미 가입된 연락처 입니다.";
        }

        if (! Strings.isEmpty(errMsg) ) {
            throw new IllegalArgumentException(errMsg);
        }

        // 신규 User 저장
        Optional<User> user = userRepository.findByUserIdAndUserSignOutYn(userDto.getUserId(), "N");
        User newUser = User.of(userDto, user.get());
        if ( userDto.getNewUserPwd() != null ) {
            newUser.setUserPwd(passwordEncoder.encode(userDto.getNewUserPwd()));
            newUser.setPwdUpdateDate(LocalDateTime.now());
        }
        newUser.setUpdateDate(LocalDateTime.now());
        userRepository.save(newUser);

        return "회원 정보 수정에 성공했습니다.";
    }
    @Override
    public String findId(UserDto userDto, HttpServletRequest request) {
        String res = "";

        String emailAddr = userDto.getUserEmailId() + "@" + userDto.getUserEmailDomain();

        List<User> userList = userRepository.findByUserEmailAndUserSignOutYn(emailAddr, "N");

        if ( userList != null && ! userList.isEmpty() ) {
            String userId = "";
            for (int i = 0; i < userList.size(); i++) {
                userId += userList.get(i).getUserId();
                if (i < userList.size() - 1) {
                    userId += ", ";
                }
            }

            MailDto mailDto = new MailDto(emailAddr, "FIND_ID", userId);

            String result = mailService.sendEmail(mailDto);

            if ( "S".equals(result) ) {
                res = "입력하신 이메일로 가입된 ID를 발송하였습니다.";
            } else {
                res = "일시적 오류로 메일 발송에 실패하였습니다. 다시 시도해주시길 바랍니다.";
            }

        } else {
            res = "입력하신 이메일로 가입된 ID를 찾을 수 없습니다.";
        }

        return res;
    }
    @Override
    public String findPw(UserDto userDto, HttpServletRequest request) {
        String res = "";

        String emailAddr = userDto.getUserEmailId() + "@" + userDto.getUserEmailDomain();

        Optional<User> userInfo = userRepository.findByUserEmailAndUserIdAndUserSignOutYn(emailAddr, userDto.getUserId(), "N");

        if ( userInfo.isPresent() ) {
            String tempPassword = generatedTempPassword(12);

            MailDto mailDto = new MailDto(emailAddr, "FIND_PW", tempPassword);

            String result = mailService.sendEmail(mailDto);

            if ( "S".equals(result) ) {
                User user = userInfo.get();

                user.setUserPwd(passwordEncoder.encode(tempPassword));
                user.setPwdUpdateDate(LocalDateTime.now());
                user.setUpdateDate(LocalDateTime.now());
                userRepository.save(user);

                res = "입력하신 이메일로 임시 비밀번호를 발송하였습니다.";
            } else {
                res = "일시적 오류로 메일 발송에 실패하였습니다. 다시 시도해주시길 바랍니다.";
            }

        } else {
            res = "입력하신 ID를 찾을 수 없습니다.";
        }

        return res;
    }
    @Override
    public String signout(HttpServletRequest request) throws SecurityException {
        String msg = "";
        String userId = null;
        String token = null;
        boolean isNewToken = false;

        // 1. header에서 token 추출
        token = jwtUtil.resolveAccessToken(request);

        // 2. token이 null인 경우 refresh token으로 재발급 처리
        if ( token == null ) {
            token = jwtUtil.resolveNewAccessToken(request);
            isNewToken = token != null;
        }

        // 3. token에서 userId 추출
        if ( token != null && jwtUtil.validateToken(token) ) {
            userId = jwtUtil.getUserIdFromToken(token);
        }

        // 4. token 검증 실패 시
        if ( token == null || userId == null ) {
            throw new SecurityException("로그인 유효기간이 만료되었습니다.\n다시 로그인 해주시길 바랍니다.");
        }

        // 5. signout 처리
        Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn(userId, "N");

        if ( exsist.isEmpty() ) {
            throw new SecurityException("유저 정보를 찾을 수 없습니다.");
        }

        User user = exsist.get();
        if ( "N".equals(user.getUserSignOutYn()) ) {
            user.setUserSignOutYn("Y");
            user.setUpdateDate(LocalDateTime.now());
            user.setUserExpiredDate(LocalDateTime.now());
            userRepository.save(user);
        }

        msg = "회원 탈퇴에 성공하였습니다.";

        return msg;
    }


    // 로그인 유저의 role 조회
    @Override
    public String getLoginUserRole(String userId) {
        return userRepository.findByUserIdAndUserSignOutYn(userId, "N").get().getUserRole();
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
    @Override
    public String findUserIdFromUserMgmtNo(Long userMgmtNo) {
        String userId = null;
        if ( userMgmtNo != null && userMgmtNo >= 0 ) {
            Optional<User> exsist = userRepository.findByUserMgmtNo(userMgmtNo);
            if ( exsist.isPresent() ) {
                User user = exsist.get();
                userId = user.getUserId();
            }
        }
        return userId;
    }

    @Override
    public Long findUserMgmtNoFromUserId(String userId) {
        Long userMgmtNo = null;
        if ( userId != null && !userId.isEmpty() && !"".equals(userId) ) {
            Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn((String) userId, "N");
            if ( exsist.isPresent() ) {
                userMgmtNo = exsist.get().getUserMgmtNo();
            }
        }
        return userMgmtNo;
    }

    @Override
    public String getEmailAddr(String userId) {
        String emailAddr = "";
        Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn(userId,"N");
        if ( exsist.isPresent() ) {
            emailAddr = exsist.get().getUserEmail();
        }
        return emailAddr;
    }

    @Override
    public List<UserDto> getUserInfo(SearchDto searchDto) {
        List<User> userList = new ArrayList<>();
        if ( !"".equals(searchDto.getSortColumn()) ) {
            Sort sort = Sort.by("DESC".equals(searchDto.getSort()) ? Sort.Direction.DESC : Sort.Direction.ASC, searchDto.getSortColumn());
            userList = userRepository.findAll(SearchSpecification.searchWiths(searchDto), sort);
        } else {
            userList = userRepository.findAll(SearchSpecification.searchWiths(searchDto));
        }
        return UserDto.of(userList);
    }

    @Override
    public String updateUserRole(String type, List<Long> targetUser, HttpServletRequest request) {
        String res = "";
        UserDto userDto = findUserInfoFromHttpRequest(request);
        int count = 0;
        if ( userDto != null ) {
            for (Long userMgmtNo : targetUser) {
                count += userRepository.updateUserRole(userMgmtNo, "admin".equals(type) ? "ROLE_ADMIN" : "ROLE_USER", userDto.getUserMgmtNo());
            };
        }
        res = count + "건의 회원정보 수정이 완료되었습니다.";
        return res;
    }

    @Override
    public String adminUserSignOut(List<Long> targetUser, HttpServletRequest request) {
        String res = "";
        UserDto userDto = findUserInfoFromHttpRequest(request);
        int count = 0;
        if ( userDto != null ) {
            for (Long userMgmtNo : targetUser) {
                count += userRepository.updateUserSignOut(userMgmtNo, userDto.getUserMgmtNo());
            };
        }
        res = count + "건의 회원이 강제 탈퇴 처리되었습니다.";
        return res;
    }

    @Override
    public UserDto findUserInfoFromHttpRequest(HttpServletRequest request) {
        UserDto dto = null;
        Map<String, Object> loginInfo = jwtUtil.getUserIdFromToken(request);

        if ( loginInfo.containsKey("userId") ) {
            Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn((String) loginInfo.get("userId"), "N");
            if ( exsist.isPresent() ) {
                dto = UserDto.of(exsist.get());
            }
        }
        return dto;
    }

    public String generatedTempPassword(int length) {
        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String DIGITS = "0123456789";
        String SYMBOLS = "!@#$%^&*()_-+=<>?";
        String ALL = UPPER + LOWER + DIGITS + SYMBOLS;

        StringBuilder password = new StringBuilder(length);

        // 최소 1개씩은 포함되도록 강제
        password.append(UPPER.charAt(secureRandom.nextInt(UPPER.length())));
        password.append(LOWER.charAt(secureRandom.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
        password.append(SYMBOLS.charAt(secureRandom.nextInt(SYMBOLS.length())));

        // 나머지 문자 채우기
        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(secureRandom.nextInt(ALL.length())));
        }

        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
