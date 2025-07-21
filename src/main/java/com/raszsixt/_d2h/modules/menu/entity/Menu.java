package com.raszsixt._d2h.modules.menu.entity;

import com.raszsixt._d2h.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name="1D2H_MENU")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class Menu {
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

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime menuRegDate;

    @Column(columnDefinition = "INT")
    private Integer menuSortOrder;

    @ManyToOne
    @JoinColumn(name="MENU_REGISTER_ID")
    private User menuRegister;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime menuUpdateDate;

    @ManyToOne
    @JoinColumn(name="MENU_UPDATER_ID")
    private User menuUpdater;
}
