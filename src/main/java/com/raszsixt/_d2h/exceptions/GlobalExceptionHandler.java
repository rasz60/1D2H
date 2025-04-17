package com.raszsixt._d2h.exceptions;

import com.raszsixt._d2h.exceptions.dto.ErrorResponse;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 예외 처리
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException usernameNotFoundException) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                        usernameNotFoundException.getMessage().isEmpty() ? "사용자를 찾을 수 없습니다." : usernameNotFoundException.getMessage()));
    }

    // 값 검증 실패 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), illegalArgumentException.getMessage()));
    }


    // 인증 실패 예외 처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialException(BadCredentialsException badCredentialsException) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                        badCredentialsException.getMessage().isEmpty() ? "아이디 또는 비밀번호가 올바르지 않습니다." : badCredentialsException.getMessage()));
    }

    // JWT 인증 예외 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException securityException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                        securityException.getMessage().isEmpty() ? "접근이 거부되었습니다." : securityException.getMessage()));
    }

    // 권한이 없는 API 접근 처리
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException authorizationDeniedException) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                        authorizationDeniedException.getMessage().isEmpty() ? "접근할 수 있는 권한이 없습니다." : authorizationDeniedException.getMessage()));
    }

    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        exception.getMessage().isEmpty() ? "서버 오류가 발생하였습니다. 다시 한 번 시도해주세요." : exception.getMessage()));
    }
}
