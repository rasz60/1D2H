## Day6. Spring Security / JWT 설정 - 4

#### 1. @PreAuthorize 설정
- API 접근 전에 권한을 체크하는 어노테이션
- SecurityConfig 클래스에 @EnableMethodSecurity 추가
- User Entity getAuthorities() 메서드 수정
  - 콤마로 연결된 형태의 userRole을 split하도록 구현
- TEST용으로 /admin-only, /user-or-admin API 구현
  - /admin-only : @PreAuthorize("hasRole('ROLE_ADMIN')")
  - /user-or-admin : @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
- User Entity의 부여된 Role에 따라 접근 가능

#### 2. Postman API 테스트
- /api/auth/login 호출하여 발급 받은 토큰을 Authroization Bearer token에 넣은 후 호출
- /api/auth/admin-only : ROLE_USER 접근 불가
- /api/auth/user-or-admin : ROLE_USER 접근 가능

#### 3. Exception 통합 처리
- GlobalExceptionHandler, ErrorResponse DTO 클래스 구현
  - Handler
    - @RestControllerAdvice 어노테이션으로 Controller에서 발생하는 Exception Filter로 지정
    - @ExceptionHandler(${ExceptionName}.class)으로 어떤 Exception을 수집할지 지정
    - DTO 객체에 코드와 메세지를 대입하여 리턴
  - DTO : errorCode, errorMessage를 가지는 객체 클래스

#### 4. Postman 호출 테스트
- /api/auth/signup
  - 아이디, 비밀번호, 이메일, 연락처 미입력 시 : 400 , 정상
  - 비밀번호 암호화 인코딩 후 저장 확인 완료
- /api/auth/login
  - 아이디, 비밀번호 오류 시 : 403 , 정상
  - 아이디, 비밀번호 일치 시 : AccessToken Return
- /api/auth/user-or-admin, /api/auth/admin-only
  - 권한 있는 경우 : 정상 접근
  - 권한 없는 경우 : 403 , 정상

