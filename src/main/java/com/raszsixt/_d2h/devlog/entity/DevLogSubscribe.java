package com.raszsixt._d2h.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_DEVLOG_SUBSCRIBE")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long itemSubsNo;

    @Column(columnDefinition = "VARCHAR(100)")
    private String itemSubsType;

    @Column(columnDefinition = "BIGINT")
    private Long itemId;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemSubsStartDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime itemSubsEndDate;


    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemSubsRegistDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemSubsRegister;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime itemSubsUpdateDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemSubsUpdater;
}
