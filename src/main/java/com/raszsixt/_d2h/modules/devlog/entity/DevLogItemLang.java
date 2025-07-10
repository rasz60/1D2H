package com.raszsixt._d2h.modules.devlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "1d2h_devlog_item_lang")
@DynamicUpdate
@DynamicInsert
@Data
public class DevLogItemLang {
    @Id
    @SequenceGenerator(
            name = "item_lang_seq_generator",
            sequenceName = "1d2h_devlog_item_lang_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "item_lang_seq_generator"
    )
    private Long itemLangId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "itemNo", name = "itemNo")
    private DevLogItem itemNo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "langId", name = "langId")
    private DevLogLang langId;
}
