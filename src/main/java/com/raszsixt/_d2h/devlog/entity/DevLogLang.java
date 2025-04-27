package com.raszsixt._d2h.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "1D2H_DEVLOG_LANG")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class DevLogLang {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long langId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "langTypeId", name = "langTypeId")
    private DevLogLangType langTypeId;// "BACK" , "FRONT", "DB", "LIBRARY", "PLUGIN", "ETC"

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langName;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String useYn;

}
