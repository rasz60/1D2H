package com.raszsixt._d2h.user.entity;

import com.raszsixt._d2h.user.dto.SignupDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private LocalDateTime userBirth;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userNation;

    @Column(columnDefinition = "VARCHAR(100)")
    private String userZipCode;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userAddr;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String userAddrDesc;

    @Column(columnDefinition="INT DEFAULT 1",nullable=false)
    private int userLevel;

    @Column(columnDefinition="VARCHAR(100)", nullable = false)
    private String userRole;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime signUpDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime firstVisitDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime latestVisitDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime pwdUpdateDate;

    @Column(columnDefinition = "VARCHAR(10)")
    private String alramYn;

    public User(String userId, String userPwd, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userPwd = userPwd;
    }

    // Dto -> Entity
    public static User of(SignupDto signupDto) {
        User newUser = new User();

        newUser.setUserId(signupDto.getSignupUserId());
        newUser.setUserEmail(signupDto.getUserEmailId() + "@" + signupDto.getUserEmailDomain());
        newUser.setUserPhone(signupDto.getUserPhone());
        newUser.setUserZipCode(signupDto.getUserZipCode());
        newUser.setUserAddr(signupDto.getUserAddr());
        newUser.setUserAddrDesc(signupDto.getUserAddrDesc());
        newUser.setUserBirth(signupDto.getUserBirth());
        newUser.setAlramYn(signupDto.isAlarmYn() ? "Y" : "N");

        return newUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(userRole.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
