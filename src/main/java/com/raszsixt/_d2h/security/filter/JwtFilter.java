package com.raszsixt._d2h.security.filter;

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
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

public class JwtFilter extends UsernamePasswordAuthenticationFilter {

    private final String secretKey = "1d2h_secret_token_very_secure_long_key_8079";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String token = resolveToken(req);

        if ( token != null && validateToken(token) ) {
            UserDetails userDetails = getUserFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities())
            );
        }

        chain.doFilter(req, res);
    }

    // request에 포함된 token 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // token 검증
    private boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //
    private Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //
    private UserDetails getUserFromToken(String token) {
        Claims claims = getClaims(token);
        String userId = claims.getSubject();
        return new User(userId, "", Collections.emptyList());
    }
}
