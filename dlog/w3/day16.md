# DAY16. Signup 마무리

#### 1. SignupDto 추가
- front에서 넘어오는 signup 정보 처리를 위해 DTO 추가
- User 컬럼과 매핑될 변수 + 중복 검사 결과를 반영할 변수 추가 선언

#### 2. 중복 검사 API 변경 (/api/auth/dupChk)
- id만 중복 확인 하는 로직에서 3가지 항목 중복 체크하도록 변경 (signupUserId, userEmail, userPhone)
- SignupDto에 중복 검사 결과 반영하여 return

#### 3. SignupDto -> User 변경 로직 추가
- 최종 회원가입 API 호출 시, SignupDto로 수신하여 User Entity로 변환 필요
- of 메서드로 대칭되는 컬럼 매핑
- userPwd : of 메서드로 매핑하지 않고 Service단에서 암호화 처리된 값으로 set
- userRole : of 메서드로 매핑하지 않고 Service 단에서 직접 set

#### 4. 회원가입 API 수정 (/api/auth/signup)
- @RequestBody SignupDto로 수신
- front 검증 완료된 정보 필수 값 (SQL 오류 방지), 중복 여부만 다시 한 번 체크 (동시 진입 방지)
- 검증 완료 후 SignupDto를 User Entity로 변환 (User.of(SignupDto)) 하여 저장

#### 5. 로그인 API 수정 (/api/auth/login)
- 해당 계정의 첫 로그인일 때, firstVisitDate 현재 시간으로 update
- 해당 계정의 마지막 로그인 일시 (latestVisitDate) 현재 시간으로 update
- firstVisitDate, latestVisitDate update 시, 불필요하게 전체 entity 저장하지 않고 필요한 컬럼만 수정
- @Query(${query})로 수정하는 방법, EntityManager로 수정하는 방법 중 @Query(${query})로 구현
```
UserRepository

.
.
.

@Modifying
@Transactional
@Query("UPDATE User u SET u.firstVisitDate = :firstVisitDate WHERE u.userMgmtNo = :userMgmtNo")
int updateFirstVisitDate(@Param("userMgmtNo") Long userMgmtNo,@Param("firstVisitDate") LocalDateTime firstVisitDate);

@Modifying
@Transactional
@Query("UPDATE User u SET u.latestVisitDate = :latestVisitDate WHERE u.userMgmtNo = :userMgmtNo")
int updateLatestVisitDate(@Param("userMgmtNo") Long userMgmtNo,@Param("latestVisitDate") LocalDateTime latestVisitDate);

.
.
.

```
- @Modifying : 업데이트 쿼리임을 명시하는 역할
- @Query(${query}) 입력 시, Table명 = Entity명 / Column명 = Entity 변수 명으로 입력
  - 실제 테이블 명으로 입력을 원하면 아래와 같이 추가 설정 필요 (nativeQuery = true)
  ```
  @Query(value = "UPDATE 1d2h_user SET FIRST_VISIT_DATE = :firstVisitDate WHERE USER_MGMT_NO = :userMgmtNo", nativeQuery = true)
  ```
  
#### 6. RefreshToken 로직 수정
- refreshToken DB 저장 시 함께 저장되는 deviceInfo 정보 강화
  - 기존 : "${os} - ${browser}"
  - 변경 : "${os} - ${browser} - ${client-ip}"
  - ${os} - ${browser}는 front 단에서, ${client-ip}는 back단에서 추출하여 insert
- refreshToken DB 저장 시, 해당 userId로 저장된 refreshToken 전체 삭제 후 마지막 device 정보만 저장하도록 변경
