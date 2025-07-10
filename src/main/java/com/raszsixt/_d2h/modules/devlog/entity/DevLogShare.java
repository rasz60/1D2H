package com.raszsixt._d2h.modules.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_DEVLOG_SHARE")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogShare {

    @Id
    @SequenceGenerator(
            name = "share_seq_generator",
            sequenceName = "1d2h_devlog_share_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "share_seq_generator"
    )
    private Long itemShareNo;

    @Column(columnDefinition = "VARCHAR(100)")
    private Long itemType;

    @Column(columnDefinition = "BIGINT")
    private Long itemId;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemShareDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemShareUserNo;

}
