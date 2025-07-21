package com.raszsixt._d2h.modules.menu.dto;

import com.raszsixt._d2h.modules.menu.entity.Menu;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class MenuDto {
    private Long menuId;
    private String menuName;
    private String menuIcon;
    private String menuAuth;
    private String menuUseYn;
    private String menuTarget;
    private String menuUrl;
    private LocalDateTime menuRegDate;
    private String menuRegisterId;
    private LocalDateTime menuUpdateDate;
    private String menuUpdaterId;
    private int menuSortOrder;
    private boolean auth;
    public static MenuDto of(Menu menu) {
        MenuDto menuDto = new MenuDto();

        menuDto.setMenuId(menu.getMenuId());
        menuDto.setMenuName(menu.getMenuName());
        menuDto.setMenuIcon(menu.getMenuIcon());
        menuDto.setMenuAuth(menu.getMenuAuth().toString());
        menuDto.setMenuUseYn(menu.getMenuUseYn());
        menuDto.setMenuTarget(menu.getMenuTarget());
        menuDto.setMenuUrl(menu.getMenuUrl());
        menuDto.setMenuRegDate(menu.getMenuRegDate());
        menuDto.setMenuRegisterId(menu.getMenuRegister().getUserId());
        menuDto.setMenuUpdateDate(menu.getMenuUpdateDate());
        menuDto.setMenuUpdaterId(menu.getMenuUpdater().getUserId());
        menuDto.setMenuSortOrder(menu.getMenuSortOrder());

        return menuDto;
    }
}
