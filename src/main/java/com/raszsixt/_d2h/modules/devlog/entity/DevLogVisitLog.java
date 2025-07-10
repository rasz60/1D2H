package com.raszsixt._d2h.modules.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_DEVLOG_VISIT_LOG")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogVisitLog {

    @Id
    @SequenceGenerator(
            name = "visit_log_seq_generator",
            sequenceName = "1d2h_devlog_visit_log_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "visit_log_seq_generator"
    )
    private Long visitId;

    @Column(columnDefinition = "BIGINT")
    private Long itemNo;

    @Column(columnDefinition = "VARCHAR(100)")
    private String itemType;

    @Column(columnDefinition = "BIGINT")
    private Long userMgmtNo;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime visitDate;

}
