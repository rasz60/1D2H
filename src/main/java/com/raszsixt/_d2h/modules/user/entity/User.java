package com.raszsixt._d2h.modules.user.entity;

import com.raszsixt._d2h.modules.user.dto.SignupDto;
import com.raszsixt._d2h.modules.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
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
    @SequenceGenerator(
            name = "user_seq_generator",
            sequenceName = "1d2h_user_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq_generator"
    )
    private Long userMgmtNo;

    @Column(columnDefinition="VARCHAR(100)",nullable=false)
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

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name="UPDATER_ID")
    private User updaterId;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime userExpiredDate;

    @Column(columnDefinition="VARCHAR(10)")
    private String userSignOutYn;

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

        if (! signupDto.getUserBirth().isEmpty() ) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(signupDto.getUserBirth(), formatter);
            LocalDateTime dateTime = date.atStartOfDay();
            newUser.setUserBirth(dateTime);
        }

        newUser.setAlramYn(signupDto.isAlarmYn() ? "Y" : "N");

        return newUser;
    }

    public static User of(UserDto userDto, User user) {
        user.setUserEmail(userDto.getUserEmailId() + "@" + userDto.getUserEmailDomain());
        user.setUserPhone(userDto.getUserPhone());
        user.setUserZipCode(userDto.getUserZipCode());
        user.setUserAddr(userDto.getUserAddr());
        user.setUserAddrDesc(userDto.getUserAddrDesc());
        if ( userDto.getUserBirth() != null && !userDto.getUserBirth().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(userDto.getUserBirth(), formatter);
            LocalDateTime dateTime = date.atStartOfDay();
            user.setUserBirth(dateTime);
        } else {
            user.setUserBirth(null);
        }
        user.setAlramYn(userDto.isAlarmYn() ? "Y" : "N");
        return user;
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
