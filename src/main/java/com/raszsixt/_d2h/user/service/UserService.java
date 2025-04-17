package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.user.dto.LoginRequestDto;
import com.raszsixt._d2h.user.dto.LoginResponseDto;
import com.raszsixt._d2h.user.dto.SignupDto;
import com.raszsixt._d2h.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface UserService {
    public String signup(SignupDto signupDto) throws IllegalArgumentException;
    public SignupDto dupChk(SignupDto signupDto);
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException;
    public LoginResponseDto authCheck(HttpServletRequest request) throws AuthenticationException;
    public void logout(HttpServletRequest request);
    public UserDto infoChk(LoginRequestDto loginRequestDto, HttpServletRequest request) throws BadCredentialsException;
    public String setUser(UserDto userDto, HttpServletRequest request)  throws IllegalArgumentException;
    public String getLoginUserRole(String userId);
}
