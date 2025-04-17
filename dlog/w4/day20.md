# DAY20. 회원 정보 수정 & 탈퇴 구현

#### 1. 비밀번호 인증 요청 API 추가 (/api/auth/infoChk)
- 정보 수정을 위해 로그인된 사용자의 비밀번호를 재검증 시 호출할 API 추가 (/api/auth/infoChk)
- userId는 token (Access, Refresh)에서 추출
  - Access Token 만료 시, Refresh Token 조회
  - Access / Refresh Token 모두 만료 시 SecurityException 발생
- 입력한 Password와 일치하는지 확인
- 이 때, PasswordEncoder.match(pwd, encodePwd) 메서드 사용
```
.
.
.
// 5. userId와 userPwd가 일치하는지 확인
        Optional<User> exsist = userRepository.findByUserIdAndUserSignOutYn(loginRequestDto.getUserId(),"N");
        if ( exsist.isEmpty() || !passwordEncoder.matches(loginRequestDto.getUserPwd(),exsist.get().getUserPwd()) ) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
.
.
.
```
- 비밀번호 인증이 완료되면 User 엔티티를 조회하여 front로 return

#### 2. 회원정보 수정 API 구현 (/api/auth/setUser)
- UserDto 추가하여 @RequestBody로 front 입력 값 bind
- UserEmail, UserPhone 필수 값 검증 및 중복 검증
- UserDto의 userId로 기존에 가입된 정보의 User 객체 조회
- 조회된 User 객체에 UserDto의 입력 값 binding
- new password 값이 있을 때
  - passwordEncoder로 encoding한 pwd binding
  - passwordUpdateDate 현재 시간으로 변경
- User 엔티티에 updateDate 추가하여 정보 수정 시 현재 시간으로 설정

#### 3. 중복 검사 API 수정
- 중복 검사 용 DupChkDto 추가
- dupChkType이 signup일 때, ID 중복 검증 아닐 때는 제외
- SignupDto, UserDto를 DupChkDto로 변환하는 of 메서드 구현