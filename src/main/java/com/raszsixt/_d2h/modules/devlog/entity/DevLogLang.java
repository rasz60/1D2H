package com.raszsixt._d2h.modules.devlog.entity;

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
    @SequenceGenerator(
            name = "lang_seq_generator",
            sequenceName = "1d2h_devlog_lang_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "lang_seq_generator"
    )
    private Long langId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "langTypeId", name = "langTypeId")
    private DevLogLangType langTypeId;// "BACK" , "FRONT", "DB", "LIBRARY", "PLUGIN", "ETC"

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String langName;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    private String useYn;

}
