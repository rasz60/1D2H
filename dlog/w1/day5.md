## Day5. Spring Security / JWT 설정 - 3

#### 1. DAY4 이슈 조치
- /api/auth/signup API 동작하지 않음
  - User Entity에 userRole 변수를 추가하고 Not Null로 지정했음
  - 기존에 User 테이블에 있던 데이터 중 userRole을 가지고 있지 않던 값이 있어 컬럼을 JPA Not Null로 생성할 수 없음
  - 입력한 정보 + userRole = "USER" 로 설정하여 SAVE
- /api/auth/login API 오류 발생
  - CustomUserDetailsService에서 loadUserByUsername return 타입이 Spring Security의 User 객체 리턴
  - User 엔티티가 이미 UserDetails 객체를 구현한 클래스이므로 User 엔티티 객체 바로 리턴하도록 변경 

#### 2. Access Token, Refresh Token 구현
- 2개의 토큰으로 인증하도록 구현
  - AccessToken
    - 유효기간을 15분으로 하고, 서버 접근 시마다 체크함
    - 만료되었을 때, token에 포함된 UserId를 추출하여 RefreshToken을 조회하도록 구현
  - RefreshToken 
    - 유효기간을 7일로 하고 DB에 UserId와 token 쌍으로 저장 (entity 생성)
    - RefreshToken이 유효하면 AccessToken을 재발급
    - RefreshToken도 만료되었을 때는 다시 로그인하도록 유도하고 DB에 있는 만료된 정보를 삭제
- RefreshToken Entity, RefreshTokenRepository 구현
- JwtUtil 안에 generateToken 메서드를 generateAccessToken, generateRefreshToken으로 나누어 생성
- JWT Token 만료 기간도 AccessToken과 RefreshToken 각기 다르게 설정 (.env, Run Configurations)
- /api/auth/refresh API는 먼저 만들어두고 아직 호출할 곳을 지정해주지는 않음. (현재는 인증이 필요한 API가 없는 상태)
- 로그인 시 refreshToken을 새로 생성하여 DB에 insert하기 전에 중복된 아이디의 refreshToken이 있으면 삭제하고 save (임시)

#### 3. UserController 리팩토링
- UserService 추가
- 테스트 용으로 controller에 구현된 로직 전체 Service layer로 이관
- Controller에서는 UserService의 결과만 리턴, Exception 처리

#### 4. PostMan 호출 테스트
- /api/auth/signup : 정상
- /api/auth/login : 정상, 로그인하는 ID로 저장된 refresh Token 있는 경우 삭제하고 신규 발급 (임시)
- /api/auth/refresh : 테스트 예정
