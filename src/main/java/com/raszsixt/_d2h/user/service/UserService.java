package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

public interface UserService {

    public String signup(Map<String, String> signupInfo) throws IllegalArgumentException;
    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws AuthenticationException;
    public LoginResponseDto refreshToken(String refreshToken) throws RuntimeException;
}
