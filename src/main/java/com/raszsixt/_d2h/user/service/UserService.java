package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public interface UserService {
    public String signup(SignupDto signupDto) throws IllegalArgumentException;
    public SignupDto dupChk(SignupDto signupDto);
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException;
    public LoginResponseDto refreshToken(String refreshToken) throws RuntimeException;
    public void logout(HttpServletRequest request);
    public String getLoginUserRole(String userId);
}
