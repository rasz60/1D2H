package com.raszsixt._d2h.security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}") // @Value = application.yml 속성 값 가져오기
    private String secretKey; // token 생성 secret key
    @Value("${jwt.expiration.time}")
    private long expirationTime; // 1일 (milliseconds)
    private Key key;
    @PostConstruct // @PostConstruct = @Value로 주입받은 값들이 초기화 된 후에 실행
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 생성
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
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
    // jwt token parsing 하여 claims 객체 리턴
    public Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // HMAC-SHA로 SECRET_KEY Byte 배열을 인코딩
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody(); // token 값을 key와 jwt parser로 확인 후 jwt claims 객체의 payload 리턴
    }
}
