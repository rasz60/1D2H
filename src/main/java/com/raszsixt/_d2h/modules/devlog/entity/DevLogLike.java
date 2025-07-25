package com.raszsixt._d2h.modules.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_DEVLOG_LIKE")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogLike {

    @Id
    @SequenceGenerator(
            name = "like_seq_generator",
            sequenceName = "1d2h_devlog_like_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "like_seq_generator"
    )
    private Long itemLikeNo;

    @Column(columnDefinition = "VARCHAR(100)")
    private String itemType;

    @Column(columnDefinition = "BIGINT")
    private Long itemId;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemLikeDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemLikeUserNo;

}
