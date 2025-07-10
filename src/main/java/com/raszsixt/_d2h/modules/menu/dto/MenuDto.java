package com.raszsixt._d2h.modules.menu.dto;

import java.time.LocalDateTime;

public class MenuDto {
    private Long menuId;
    private String menuName;
    private String menuIcon;
    private String menuAuth;
    private String menuUseYn;
    private LocalDateTime menuRegDate;
    private String menuRegisterId;
    private LocalDateTime menuUpdateDate;
    private String menuUpdaterId;

    private boolean auth;
}
