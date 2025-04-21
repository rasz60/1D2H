package com.raszsixt._d2h.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

import java.util.List;

@Entity
@Table(name = "1D2H_DEVLOG_GROUP")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "group_no")
    private Long groupNo;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String groupTitle;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime groupRegistDate;

    @Column(columnDefinition = "BIGINT")
    private Long groupRegister;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime groupUpdateDate;

    @Column(columnDefinition = "BIGINT")
    private Long groupUpdater;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String progress;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String openYn;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String deleteYn;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "group_no")
    private List<DevLogItem> itemList;
}
