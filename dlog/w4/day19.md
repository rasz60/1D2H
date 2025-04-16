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

