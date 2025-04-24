package com.raszsixt._d2h.devlog.entity;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long itemLikeNo;

    @Column(columnDefinition = "VARCHAR(100)")
    private String itemLikeType;

    @Column(columnDefinition = "BIGINT")
    private Long itemId;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime itemLikeDate;

    @Column(columnDefinition = "BIGINT")
    private Long itemLikeUserNo;

}
