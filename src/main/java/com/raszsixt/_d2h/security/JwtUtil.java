package com.raszsixt._d2h.security;

import com.raszsixt._d2h.security.entity.RefreshToken;
import com.raszsixt._d2h.security.repository.RefreshTokenRepository;
import com.raszsixt._d2h.modules.user.entity.User;
import com.raszsixt._d2h.modules.user.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}") // @Value = application.yml 속성 값 가져오기
    private String secretKey; // token 생성 secret key
    @Value("${jwt.expiration.access}")
    private long accessExpirationTime; // 15분 (milliseconds)
    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationTime; // 7일 (milliseconds)
    private Key key;

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    public JwtUtil(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct // @PostConstruct = @Value로 주입받은 값들이 초기화 된 후에 실행
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // request에 포함된 token 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authoriztion Header 추출
        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) { // Header에 Authorization이 있고, Bearer로 시작할 때
            return bearerToken.substring(7); // Bearer 이 후로 온 값 추출
        }
        return null;
    }
    // Refresh Token으로 새로운 access token 발급
    public String resolveNewAccessToken(HttpServletRequest request) {
        String newAccessToken = null;
        String refreshToken = null;
        String userId = null;

        refreshToken = resolveRefreshToken(request);

        if ( refreshToken != null && this.validateToken(refreshToken) ) {
            userId = getUserIdFromToken(refreshToken);
            Optional<User> user = userRepository.findByUserIdAndUserSignOutYn(userId, "N");

            if (user.isPresent()) {
                newAccessToken = generateAccessToken(userId);
            }
        }

        return newAccessToken;
    }


    // Refresh Token 조회
    public String resolveRefreshToken(HttpServletRequest request) {
        // header에 device info 추출
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        String device = request.getHeader("X-device-info");
        String deviceInfo = device + " - " + ip;

        Optional<RefreshToken> exists = refreshTokenRepository.findFirstByDeviceInfoOrderByRegDateDesc(deviceInfo);
        if (exists.isPresent()) {
            return exists.get().getRefreshToken();
        }
        return null;
    }

    // JWT ACCESS TOKEN 생성
    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT REFRESH TOKEN 생성
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // token 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // token에 담긴 claims 객체에서 userId 조회
    public String getUserIdFromToken(String token) {
        Claims claims = null;
        String userId = "";
        try {
            claims = getClaims(token); // token에 해당되는 claims 객체
        } catch (ExpiredJwtException e) {
            claims = e.getClaims(); // ✅ 만료된 토큰에서 claims 꺼내기
        } finally {
            userId = claims != null ? claims.getSubject() : ""; // claims 객체에 포함된 userId
        }
        return userId;
    }

    // jwt token parsing 하여 claims 객체 리턴
    public Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // HMAC-SHA로 SECRET_KEY Byte 배열을 인코딩
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody(); // token 값을 key와 jwt parser로 확인 후 jwt claims 객체의 payload 리턴
    }

    // jwt token으로 id 가져오기
    public Map<String, Object> getUserIdFromToken(HttpServletRequest request) {
        Map<String, Object> rst = new HashMap<>();

        String token = null;
        String refreshToken = null;
        String userId = null;
        String role = "guest";

        // 1. request에서 Access Token 추출
        token = this.resolveAccessToken(request);

        // 2. request Access Token 존재하지만, 유효하지 않을 때
        if ( token != null && ! this.validateToken(token) ) {
            // 2-1. token 초기화
            token = null;
        }

        // 3. request에 Access Token이 없는 경우
        if ( token == null ) {
            // 3-1. Refresh Token 조회하여 새로운 Access Token 발급
            token = this.resolveNewAccessToken(request);
            if ( token != null ) rst.put("new-access-token", token);
        }

        // 4. token에서 추출한 userId에 userRole 추가
        if ( token != null && this.validateToken(token)) {
            userId = this.getUserIdFromToken(token);
            rst.put("userId", userId);
        }

        return rst;
    }

}
