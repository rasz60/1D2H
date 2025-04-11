# DAY17. 메뉴 구현

#### 1. Signup 마지막 처리
- "yyyy/M/d" 형태의 userBirth 를 LocalDateTime 로 변환하여 저장

#### 2. Security 설정
- menu 관련 API는 모든 권한의 유저가 호출하므로 premitAll() 설정
```
.
.
.
// Session
httpSecurity
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Spring Security가 세션을 생성하지 않고, 기존 것을 사용하지도 않음 (JWT)
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/menu/**").permitAll() // /api/auth/, /api/menu/ 하위 모든 API는 Permission All
                .anyRequest().authenticated() // 나머지 API는 모두 Permission 필요
        )
;
.
.
.
```

#### 3. Entity 추가
- menu 화면에 출력할 mui 아이콘 이름을 DB에 저장
- menu 접근 권한 User 객체의 userRole 기준으로 처리
  - Principal 이 null 일 때 : menuAuth = 0
  - userRole == "ROLE_USER" : menuAuth = 1
  - userRole == "ROLE_ADMIN" : menuAuth = 2
  - 로그인 user의 userRole을 기준으로 매겨진 menuAuth보다 작은 메뉴만 출력

#### 4. Service, Repository 추가
- UserService
  - getLoginUserRole() : 메뉴 조회 API 호출 시, Principal 의 userId 로 로그인한 유저의 Role 조회
- MenuRepository
  - findByMenuUseYnAndMenuAuthLessThan(menuUseYn, menuAuth)
    - 사용 중인 메뉴이고, userRole에 따른 menuAuth보다 작은 메뉴만 조회
