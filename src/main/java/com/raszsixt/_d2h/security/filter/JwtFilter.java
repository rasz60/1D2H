package com.raszsixt._d2h.security.filter;

import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.modules.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class JwtFilter extends OncePerRequestFilter {

    // token 조회 제외 URL
    private static final Set<String> EXCLUEDE_URLS = Set.of( "/api/auth", "/api/menu", "/api/dlog" );

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) { // 생성자 주입
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // token 조회 제외 URL인지 확인
        if ( EXCLUEDE_URLS.stream().anyMatch(path::startsWith) ) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // token 체크
        String token = null;
        String newToken = null;
        String userId = null;

        token = jwtUtil.resolveAccessToken(request);

        if ( token == null ) {
            // refreshToken 조회하여 새로운 access Token 발급
            newToken = jwtUtil.resolveNewAccessToken(request);
        }

        // refresh Token 으로 access token 발급 실패 시
        if ( token == null ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        // token 검증 성공 시
        if ( jwtUtil.validateToken(token) ) {
            // token에서 추출된 userId가 없을 때,
            userId = jwtUtil.getUserIdFromToken(token);
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
