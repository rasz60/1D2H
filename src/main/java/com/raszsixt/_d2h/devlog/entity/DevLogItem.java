package com.raszsixt._d2h.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_DEVLOG_ITEM")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long itemNo;

    @ManyToOne
    @JoinColumn(referencedColumnName = "group_no", name = "group_no")
    private DevLogGroup groupNo;

    @Column(columnDefinition = "INT")
    private Long itemSortNo;

    @Column(columnDefinition = "INT")
    private String itemType; // "DEV", "ISSUE", "INFO"

    @Column(columnDefinition = "VARCHAR(200)", nullable = false)
    private String itemTitle;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemRegistDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemRegister;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime itemUpdatetDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemUpdater;

    @Column(columnDefinition = "VARCHAR(200)", nullable = false)
    private String progress;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String openYn;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String deleteYn;
}
