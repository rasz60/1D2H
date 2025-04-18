# DAY20. 회원 정보 수정 & 탈퇴 구현 - 2

#### 1.중복 검사 API 추가 수정
- userEmail, userPhone 검사 시 탈퇴한 회원 정보는 조회하지 않도록 변경

#### 2. 회원 탈퇴 API 추가 (/api/auth/signout)
- 회원 탈퇴 시, 탈퇴 관련 상태를 UPDATE하는 API 추가
  - userSignOutYn : 탈퇴 회원 = 'Y'
  - userExpiredDate : 회원 탈퇴 일자
  - updateDate : 회원정보 수정 일자
- token 조회 실패 or userId로 회원 정보 조회 실패 시 SecurityException 발생
- UserRepository signout 처리 메서드 추가
```
@Modifying
@Transactional
@Query("UPDATE User u SET u.userSignOutYn = :userSignOutYn, u.userExpiredDate = :userExpiredDate WHERE u.userMgmtNo = :userMgmtNo")
int updateUserSignOut(@Param("userMgmtNo") Long userMgmtNo,@Param("userSignOutYn") String userSignOutYn, @Param("userExpiredDate") LocalDateTime userExpiredDate);
```

#### 3. 회원 탈퇴 flag 처리
- 로그인 시, authenticationManager를 통해 UserDetails 객체를 만들어 Principal에 저장하는 로직 수정
- CustomUserDetails 에서 findByUserId가 아닌 findByUserIdAndUserSignOutYn을 호출하여 회원 탈퇴한 아이디는 제외하고 조회
```
CustomUserDetailsService.java
.
.
.
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // USERID 존재 여부 확인 후 userDetails 객체로 return
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUserIdAndUserSignOutYn(username, "N");
        if ( userOptional.isEmpty() ) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }

        return userOptional.get();
    }
}
```