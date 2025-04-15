package com.raszsixt._d2h.menu.entity;

import com.raszsixt._d2h.user.entity.User;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @ManyToOne
    @JoinColumn(name="MENU_REGISTER_ID")
    private User menuRegister;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime menuUpdateDate;

    @ManyToOne
    @JoinColumn(name="MENU_UPDATER_ID")
    private User menuUpdater;
}
