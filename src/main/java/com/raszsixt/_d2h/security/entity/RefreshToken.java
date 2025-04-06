package com.raszsixt._d2h.security.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name="1D2H_REFRESH_TOKEN")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long refreshTokenId;

    @Column(columnDefinition = "VARCHAR(100)", unique = true)
    private String userId;

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    private String refreshToken;

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    private String deviceInfo;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate;

    public RefreshToken (String userId, String refreshToken, String deviceInfo) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.deviceInfo = deviceInfo;
    }
}
