package com.raszsixt._d2h.modules.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "1d2h_devlog_lang_type")
public class DevLogLangType {
    @Id
    @SequenceGenerator(
            name = "lang_type_seq_generator",
            sequenceName = "1d2h_devlog_lang_type_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "lang_type_seq_generator"
    )
    private Long langTypeId;
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langType;
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langColor;
}
