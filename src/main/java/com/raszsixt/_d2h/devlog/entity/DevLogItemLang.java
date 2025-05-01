package com.raszsixt._d2h.devlog.entity;

import com.raszsixt._d2h.devlog.dto.DevLogItemLangDto;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long itemLangId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "itemNo", name = "itemNo")
    private DevLogItem itemNo;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "langId", name = "langId")
    private DevLogLang langId;
}
