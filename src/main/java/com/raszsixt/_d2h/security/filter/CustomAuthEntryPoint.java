package com.raszsixt._d2h.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raszsixt._d2h.global.exceptions.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = new ObjectMapper().writeValueAsString(new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다. 다시 로그인해주세요."));

        response.getWriter().write(json);
    }
}
