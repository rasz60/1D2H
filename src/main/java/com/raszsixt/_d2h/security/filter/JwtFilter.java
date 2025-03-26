package com.raszsixt._d2h.security.filter;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil; // jwtUtil 생성자 주입
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String token = resolveToken(req); // request에서 token 추출

        if ( token != null && jwtUtil.validateToken(token) ) { // token 검증 성공 시
            UserDetails userDetails = getUserFromToken(token); // token으로 조회된 User 객체
            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities()) // JWT 토큰 생성
            );
        }
        chain.doFilter(req, res);
    }

    // request에 포함된 token 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authoriztion Header 추출
        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) { // Header에 Authorization이 있고, Bearer로 시작할 때
            return bearerToken.substring(7); // Bearer 이 후로 온 값 추출
        }
        return null;
    }

    //
    private UserDetails getUserFromToken(String token) {
        Claims claims = jwtUtil.getClaims(token); // token에 해당되는 claims 객체
        String userId = claims.getSubject(); // claims 객체에 포함된 userId
        return new User(userId, "", Collections.emptyList()); // User 객체 리턴
    }
}
