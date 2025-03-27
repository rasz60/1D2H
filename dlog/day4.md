## Day4. Spring Security / JWT 설정 - 2

#### 1. CustomUserDetailsService 구현
- JwtFilter 기존 코드에서는 token에 포함된 UserId를 추출하여 User 객체를 생성하는 방식으로 인증처리
- token에 포함된 UserId가 현재 시스템에서 어떤 상태인지 반영할 수 없음.
- Spring Security에서 인증 처리를 하는 UserDetailsService 를 상속하여 CustomUserDetailsService 구현
- token에 포함된 UserId를 추출한 후 DB에서 현재 상태 (역할, 탈퇴 여부 등)을 조회하여 리턴
- DB 조회 결과 Null 처리도 해당 클래스에서 수행

#### 2. LoginRequestDto, LoginResponseDto 구현
- Login에 필요한 Request, Response Dto 구현하여 캡슐화

#### 3. JwtFilter 수정 (모든 요청에 대한 Filter로 변환)
- 기존 소스는 login API에만 유효하도록 테스트로 작성
- Spring Security 인증 규격에 맞게 UserDetailsService 를 통해 리턴하도록 변경
- 

#### 4. SecurityConfig 추가

#### 5. Login API 수정

#### 6. Postman 테스트