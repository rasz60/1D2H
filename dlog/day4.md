## Day4. Spring Security / JWT 설정 - 2

#### 1. CustomUserDetailsService 구현
- JwtFilter 기존 코드에서는 token에 포함된 UserId를 추출하여 User 객체를 생성하는 방식으로 인증처리
- token에 포함된 UserId가 현재 시스템(DB)에서 어떤 상태인지 반영되지 않은 객체를 리턴하고 있음
- Spring Security에서 인증 처리를 하는 UserDetailsService 를 상속하여 CustomUserDetailsService 구현
- token에 포함된 UserId를 추출한 후 DB에서 현재 상태 (역할, 탈퇴 여부 등)을 조회하여 리턴
- DB 조회 결과 Null 처리도 해당 클래스에서 구현

#### 2. LoginRequestDto, LoginResponseDto 구현
- Login에 필요한 Request, Response Dto 구현하여 캡슐화

#### 3. JwtFilter 수정
- UserDetailsService 를 통해 UserDetails 객체를 리턴하도록 변경
- Spring Security 구조적인 규격에 맞춘 것도 있지만, OAuth2, 소셜 로그인 등 차후 확장성 측면에서도 UserDetails로 리턴하는 것이 유리함

#### 4. CustomAuthEntryPoint 클래스 구현
- Spring Security에서 인증 오류 발생 시, 401 Exception 발생
- Exception 발생 시 SecurityConfig.java 에 httpSecurity.exceptionHandling에 등록된 클래스에서 에러처리 가능
- response에 json 형태로 리턴하도록 CustomAuthEntryPoint 구현

#### 5. SecurityConfig 추가
- httpSecurity 설정에 exceptionHandling 추가
- CustomAuthEntryPoint에서 에러처리를 하도록 등록
- AuthenticationProvider, AuthenticationManager, BCryptPasswordEncoder Bean 등록

#### 6. Login API 수정
- LoginRequestDto를 통해 request parameter를 수집하고, LoginResponse 객체로 Login 정보를 return

#### 7. Postman 테스트
- /api/auth/login -> 무조건 Bad Credentials 발생하는 이슈!!✨ 해결필요