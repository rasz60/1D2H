package com.raszsixt._d2h.modules.user.repository;

import com.raszsixt._d2h.modules.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserMgmtNo(Long userMgmtNo);
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
    @Query("UPDATE User u SET u.userSignOutYn = 'Y', u.userExpiredDate = CURRENT_TIMESTAMP, u.updateDate = CURRENT_TIMESTAMP, u.updaterId.userMgmtNo = :updaterId WHERE u.userMgmtNo = :userMgmtNo")
    int updateUserSignOut(@Param("userMgmtNo") Long userMgmtNo, @Param("updaterId") Long updaterId);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userRole = :userRole, u.updateDate = CURRENT_TIMESTAMP, u.updaterId.userMgmtNo = :updaterId WHERE u.userMgmtNo = :userMgmtNo")
    int updateUserRole(@Param("userMgmtNo") Long userMgmtNo, @Param("userRole") String userRole, @Param("updaterId") Long updaterId);

    List<User> findByUserEmailAndUserSignOutYn(String userEmail, String userSignOutYn);
    Optional<User> findByUserEmailAndUserIdAndUserSignOutYn(String userEmail, String userId, String userSignOutYn);

    List<User> findByUserIdContains(String userId);
    List<User> findByUserEmailContains(String userEmail);
    List<User> findByUserPhoneContains(String userPhone);
    List<User> findByUserRole(String userRole);

    List<User> findByUserIdContainsAndUserSignOutYn(String userId, String userSignOutYn);
    List<User> findByUserEmailContainsAndUserSignOutYn(String userEmail, String userSignOutYn);
    List<User> findByUserPhoneContainsAndUserSignOutYn(String userPhone, String userSignOutYn);
    List<User> findByUserRoleAndUserSignOutYn(String userRole, String userSignOutYn);
}
