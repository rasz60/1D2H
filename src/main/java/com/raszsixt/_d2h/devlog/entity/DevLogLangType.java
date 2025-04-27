package com.raszsixt._d2h.devlog.entity;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long langTypeId;
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langType;
    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langColor;
}
