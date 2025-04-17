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
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserPhone(String userPhone);
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
}
