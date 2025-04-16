# DAY19. Refresh Token 추가 & 권한 확인 구현

#### 1. 권한 확인 api 추가 (/api/auth/check)
- Router(URL) 변경 시마다 해당 API를 호출하여 Token을 검증
- 유효한 토큰일 때, token과 auth return
  - token에서 추출한 userId의 userRole 기반 auth를 생성
    - login하지 않은 상태 : auth = 1
    - ROLE_USER : auth = 2
    - ROLE_ADMIN : auth = 3
- AccessToken이 유효하지 않을 때
  - refresh token 유효 시, Access Token 재발급
  - 신규 Access Token과 auth 생성하여 return
  - refresh token 만료 시, token = null, auth = 1 return
- LoginResponseDto에 auth 변수 추가
- /api/auth/login 결과에도 auth 생성하여 return

#### 2. 회원 탈퇴 flag 추가
- User Entity : USER_EXPIRED_DATE, SIGN_OUT_YN 추가
- 새로운 Access Token 발급 시, SIGN_OUT_YN이 'N'인 user를 조회하도록 구현

#### 3. 새로운 Access Token 발급 메서드 구현
- jwtUtil.resolveNewAccessToken()
- axios header에서 access Token 추출 실패 시
- refresh Token을 조회하여 새로운 access token을 발급하는 메서드
- refresh token이 만료이거나, refresh token에서 추출한 userId가 탈퇴했을 경우, null로 return

#### 4. 오류 수정
- 회원가입 시, SignUpDto의 userBirth를 localDateTime으로 변환하는 로직 null 처리 추가
```
User.java


.
.
.
// Dto -> Entity
  public static User of(SignupDto signupDto) {
      User newUser = new User();

      newUser.setUserId(signupDto.getSignupUserId());
      newUser.setUserEmail(signupDto.getUserEmailId() + "@" + signupDto.getUserEmailDomain());
      newUser.setUserPhone(signupDto.getUserPhone());
      newUser.setUserZipCode(signupDto.getUserZipCode());
      newUser.setUserAddr(signupDto.getUserAddr());
      newUser.setUserAddrDesc(signupDto.getUserAddrDesc());

      if (! signupDto.getUserBirth().isEmpty() ) {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
          LocalDate date = LocalDate.parse(signupDto.getUserBirth(), formatter);
          LocalDateTime dateTime = date.atStartOfDay();
          newUser.setUserBirth(dateTime);
      }

      newUser.setAlramYn(signupDto.isAlarmYn() ? "Y" : "N");

      return newUser;
  }
.
.
.
```
- 로그아웃 시 refresh token 삭제 로직 중 Header에 Access Token이 없을 때 처리 추가
- 메뉴 조회 시 user 권한 체크 로직 중 Header에 Access Token이 없을 때 처리 추가