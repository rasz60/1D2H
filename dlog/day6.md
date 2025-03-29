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
