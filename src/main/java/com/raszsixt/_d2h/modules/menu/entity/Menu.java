package com.raszsixt._d2h.modules.menu.entity;

import com.raszsixt._d2h.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@Table(name="1D2H_MENU")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Menu extends BaseEntity {
    @Id
    @SequenceGenerator(
            name = "menu_seq_generator",
            sequenceName = "1d2h_menu_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "menu_seq_generator"
    )
    private Long menuId;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String menuName;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String menuIcon;

    @Column(columnDefinition = "VARCHAR(100)")
    private String menuUrl;

    @Column(columnDefinition = "INT DEFAULT 0", nullable = false)
    private Integer menuAuth;

    @Column(columnDefinition = "VARCHAR(10)")
    private String menuUseYn;

    @Column(columnDefinition = "VARCHAR(100)")
    private String menuTarget;

    @Column(columnDefinition = "INT")
    private Integer menuSortOrder;
}
