## Day3. Spring Security / JWT 설정

#### 1. JWT 라이브러리 추가
- build.gradle : jwt, bCrypt 라이브러리 추가

#### 2. User Entity, Repository 추가
- Entity : UserDetails implements 받는 클래스로 구현
- UserRepository 신규 생성

#### 3. JWT 관련 클래스 추가
- com.raszsixt._d2h.security 하위 JWT 관련 클래스 추가
- JwtUtil.java : jwt 토큰 생성, 검증, request 내에 token 값으로 User 조회 등 JWT Token 관리 기능 구현
- filter.JwtFilter.java : HttpSecrity로 설정되어 permit 체크가 필요한 api 호출 시 실행할 filter servlet class
- filter.JwtAuthenticationToken.java : SecurityContextHolder에 현재 JWT token으로 검증된 사용자 정보를 저장
- .env, Run Configuration에 속성 값 추가 : JWT_SECRET_KEY, JWT_EXPIRATION_TIME

#### 4. Security Configuration 추가
- com.raszsixt._d2h.security.config 하위 클래스 추가
- csrf, formLogin, httpBasic disabled 처리
- SessionCreationPolicy.STATELESS 처리 (Spring Security가 세션을 생성/사용 하지 않음)
- /api/auth/* 하위만 권한 체크 제외, 나머지는 권한 체크 등록 (차차 늘려갈 예정)
- filter로 JwtFilter 클래스 등록, request 접근 시 해당 클래스 호출 

#### 5. Controller 추가
- /api/auth/signup, /api/auth/login API Controller 추가

#### 6. Postman 테스트
- /api/auth/signup : 호출 성공, Not Null 변수로 인해 insert exception
- /api/auth/login : 호출 성공, return 성공