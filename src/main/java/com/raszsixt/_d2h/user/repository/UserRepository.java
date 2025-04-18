package com.raszsixt._d2h.user.repository;

import com.raszsixt._d2h.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserEmailAndUserSignOutYnAndUserIdNot(String userEmail, String userSignOutYn, String userId);
    Optional<User> findByUserPhoneAndUserSignOutYnAndUserIdNot(String userPhone, String userSignOutYn, String userId);
    Optional<User> findByUserIdAndUserSignOutYn(String userId, String userSignOutYn);
    Optional<User> findByUserIdAndUserPwdAndUserSignOutYn(String userId, String userPwd, String userSignOutYn);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstVisitDate = :firstVisitDate WHERE u.userMgmtNo = :userMgmtNo")
    int updateFirstVisitDate(@Param("userMgmtNo") Long userMgmtNo,@Param("firstVisitDate") LocalDateTime firstVisitDate);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.latestVisitDate = :latestVisitDate WHERE u.userMgmtNo = :userMgmtNo")
    int updateLatestVisitDate(@Param("userMgmtNo") Long userMgmtNo,@Param("latestVisitDate") LocalDateTime latestVisitDate);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userSignOutYn = :userSignOutYn, u.userExpiredDate = :userExpiredDate WHERE u.userMgmtNo = :userMgmtNo")
    int updateUserSignOut(@Param("userMgmtNo") Long userMgmtNo,@Param("userSignOutYn") String userSignOutYn, @Param("userExpiredDate") LocalDateTime userExpiredDate);
}
