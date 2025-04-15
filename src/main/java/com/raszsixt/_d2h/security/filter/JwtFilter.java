package com.raszsixt._d2h.security.filter;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) { // 생성자 주입
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String newToken = null;
        String userId = null;

        token = jwtUtil.resolveAccessToken(request);

        if ( token == null ) {
            // refreshToken 조회
            token = jwtUtil.resolveRefreshToken(request);

            // refreshToken이 존재하는 경우
            if ( token != null && jwtUtil.validateToken(token) ) {
                // 토큰에 포함된 userId 추출하여 신규 access token 생성
                userId = jwtUtil.getUserIdFromToken(token);
                newToken = jwtUtil.generateAccessToken(userId);

            }
        }

        if ( token != null && jwtUtil.validateToken(token) ) { // token 검증 성공 시
            // token에서 추출된 userId가 없을 때,
            if (userId == null) {
                userId = jwtUtil.getUserIdFromToken(token);
            }
            User user = (User) userDetailsService.loadUserByUsername(userId);
            
            // 신규 access token이 있을 때
            if (newToken != null) {
                response.setHeader("new-access-token", newToken);
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()) // JWT 토큰
            );
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }

}
