package com.raszsixt._d2h.security.repository;

import com.raszsixt._d2h.security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(String userId);

    Optional<RefreshToken> findByUserIdAndDeviceInfo(String userId, String deviceInfo);
}
