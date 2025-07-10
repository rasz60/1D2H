package com.raszsixt._d2h.modules.devlog.entity;

import com.raszsixt._d2h.modules.devlog.dto.DevLogItemDto;
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
    @SequenceGenerator(
            name = "item_seq_generator",
            sequenceName = "1d2h_devlog_item_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "item_seq_generator"
    )
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

        item.setItemNo(devLogItemDto.getItemNo());
        item.setItemType("DLI");
        item.setItemTitle(devLogItemDto.getItemTitle());
        item.setItemContents(devLogItemDto.getItemContents());

        return item;
    }


}
