# DAY18. Refresh Token 추가 구현

#### 1. Access Token 재발급 처리
- Access Token Header에 포함되어있지 않은 상태로 API 호출했을 때
    - jwtUtils.java > resolveRefreshToken()
        - request Header 를 조합한 deviceInfo로 가장 최근에 저장된 Refresh Token 조회
        - Refresh Token이 있으면 token return, 없으면 null return
    - jwtFilter.java
        - Refresh Token이 있을 때
            - token에 포함된 userId를 추출하여 Access Token 재발급
            - token을 검증하여 만료되지 않았으면 response에 new-access-token 추가
            - filter 통과
        - Refresh Token이 없을 때 or token 이 만료되었을 때
            - 401 오류 발생

#### 2. Filter 로직 제외 URL 추가
- jwtFilter에서 인증이 필요없는 URL 처리 필요
- SecurityConfig에 인증이 필요없는 URL을 permitAll() 처리했지만 jwtFilter에서는 상관없이 모두 token 확인
- EXCLUEDE_URLS set 추가 후 token 체크 전 확인하는 로직 추가
```
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
import java.util.Set;

public class JwtFilter extends OncePerRequestFilter {

    // token 조회 제외 URL
    private static final Set<String> EXCLUEDE_URLS = Set.of( "/api/auth", "/api/menu" );

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
            // refreshToken 조회
            token = jwtUtil.resolveRefreshToken(request);

            // refreshToken이 존재하는 경우
            if ( token != null && jwtUtil.validateToken(token) ) {
                // 토큰에 포함된 userId 추출하여 신규 access token 생성
                userId = jwtUtil.getUserIdFromToken(token);
                newToken = jwtUtil.generateAccessToken(userId);
            }
        }

        if ( token == null ) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

        if ( jwtUtil.validateToken(token) ) { // token 검증 성공 시
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
```

- EXCLUEDE_URLS로 List, Map을 쓸 수도 있는데 왜 Set 일까..? (ChatGpt)
    - List : index 기반으로 접근할 때 유리
    - Map : key, value 쌍일 때, key를 기반으로 value를 찾아내는 데 유리
    - Set : 단순히 포함 여부만 필요할 때 유리