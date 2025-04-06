package com.raszsixt._d2h.security;

import com.raszsixt._d2h.user.entity.User;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}") // @Value = application.yml 속성 값 가져오기
    private String secretKey; // token 생성 secret key
    @Value("${jwt.expiration.access}")
    private long accessExpirationTime; // 15분 (milliseconds)
    @Value("${jwt.expiration.refresh}")
    private long refreshExpirationTime; // 7일 (milliseconds)
    private Key key;
    @PostConstruct // @PostConstruct = @Value로 주입받은 값들이 초기화 된 후에 실행
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // request에 포함된 token 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authoriztion Header 추출
        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) { // Header에 Authorization이 있고, Bearer로 시작할 때
            return bearerToken.substring(7); // Bearer 이 후로 온 값 추출
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

}
