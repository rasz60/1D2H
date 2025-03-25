package com.raszsixt._d2h.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_USER")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Data
public class User {

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
}
