package com.raszsixt._d2h.devlog.entity;

import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Column(columnDefinition = "VARCHAR(10)")
    private String itemType; // "DEV", "ISSUE", "INFO"

    @Column(columnDefinition = "VARCHAR(200)", nullable = false)
    private String itemTitle;

    @Column(columnDefinition = "TEXT")
    private String itemContents;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemRegistDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemRegister;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime itemUpdateDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemUpdater;

    @Column(columnDefinition = "VARCHAR(200)", nullable = false)
    private String progress;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String openYn;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String deleteYn;

    public static DevLogItem of(DevLogItemDto devLogItemDto) {
        DevLogItem item = new DevLogItem();


        return item;
    }


}
