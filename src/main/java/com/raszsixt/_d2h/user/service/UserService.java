package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public interface UserService {

    public String signup(Map<String, String> signupInfo) throws IllegalArgumentException;
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException;
    public LoginResponseDto refreshToken(String refreshToken) throws RuntimeException;
    public void logout(HttpServletRequest request);
    public int idDupChk(String userId);
}
