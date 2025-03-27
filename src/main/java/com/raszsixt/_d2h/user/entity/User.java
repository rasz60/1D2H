package com.raszsixt._d2h.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "1D2H_USER")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userMgmtNo;

    @Column(columnDefinition="VARCHAR(100)",unique=true,nullable=false)
    private String userId;

    @Column(columnDefinition="VARCHAR(1000)",nullable=false)
    private String userPwd;

    @Column(columnDefinition="VARCHAR(1000)",nullable=false)
    private String userEmail;

    @Column(columnDefinition="VARCHAR(11)",nullable=false)
    private String userPhone;

    @Column(columnDefinition="TIMESTAMP")
    private String userBirth;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userNation;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userAddr;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userAddrDesc;

    @Column(columnDefinition="INT DEFAULT 1",nullable=false)
    private int userLevel;

    @Column(columnDefinition="VARCHAR(100)", nullable = false)
    private String userRole;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime signInDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime firstVisitDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime latestVisitDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime pwdUpdateDate;

    /*
    // 알람 설정 여부, Alram Entity 생성 후 userId 기준 @OneToMany 가져올 예정
    @OneToMany(mappedBy = "", fetch=FetchType.LAZY)
    private List<Alram> alramYn;
     */

    public User(String userId, String userPwd, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userPwd = userPwd;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return userPwd;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
