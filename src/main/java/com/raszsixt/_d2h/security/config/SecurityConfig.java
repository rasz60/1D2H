package com.raszsixt._d2h.security.config;

import com.raszsixt._d2h.security.service.CustomUserDetailsService;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.security.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil; // JwtFilter 객체 생성을 위해 JwtUtil 의존성 주입
    private final CustomUserDetailsService customUserDetailsService;

    private JwtFilter jwtFilter;
    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = new JwtFilter(jwtUtil, customUserDetailsService); // JwtFilter 객체 주입
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Spring security
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // 교차 사이트 요청 위조 방지 (Cross Site Request Forgery) 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable) // Spring Security Form Login 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable) // Spring security username, password 방식의 인증 절차 사용하지 않음
        ;

        // Session
        httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Spring Security가 세션을 생성하지 않고, 기존 것을 사용하지도 않음 (JWT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // /api/auth/ 하위 모든 API는 Permission All
                        .anyRequest().authenticated() // 나머지 API는 모두 Permission 필요
                )
        ;

        // Filter
        httpSecurity
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // JWT 토큰 처리 filter 적용
        ;

        return httpSecurity.build();
    }

    //
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(createPasswordEncoder());
        return provider;
    }

    // Password Encoder 생성
    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
