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
        String token = resolveToken(request);
        if ( token != null && jwtUtil.validateToken(token) ) { // token 검증 성공 시
            String userId = jwtUtil.getUserIdFromToken(token);
            User user = (User) userDetailsService.loadUserByUsername(userId);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()) // JWT 토큰
            );
        }
        filterChain.doFilter(request, response);
    }

    // request에 포함된 token 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authoriztion Header 추출
        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) { // Header에 Authorization이 있고, Bearer로 시작할 때
            return bearerToken.substring(7); // Bearer 이 후로 온 값 추출
        }
        return null;
    }

}
