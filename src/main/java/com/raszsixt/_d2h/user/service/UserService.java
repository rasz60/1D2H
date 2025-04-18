package com.raszsixt._d2h.user.service;

import com.raszsixt._d2h.user.dto.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface UserService {
    public String signup(SignupDto signupDto) throws IllegalArgumentException;
    public DupChkDto dupChk(DupChkDto signupDto);
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) throws AuthenticationException;
    public LoginResponseDto authCheck(HttpServletRequest request) throws AuthenticationException;
    public void logout(HttpServletRequest request);
    public UserDto infoChk(LoginRequestDto loginRequestDto, HttpServletRequest request) throws BadCredentialsException, SecurityException;
    public String setUser(UserDto userDto, HttpServletRequest request)  throws IllegalArgumentException;
    public String signout(HttpServletRequest request) throws SecurityException;
    public String getLoginUserRole(String userId);
}
