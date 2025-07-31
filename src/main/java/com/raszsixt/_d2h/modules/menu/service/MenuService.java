package com.raszsixt._d2h.modules.menu.service;

import com.raszsixt._d2h.modules.menu.dto.MenuDto;
import com.raszsixt._d2h.modules.menu.entity.Menu;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface MenuService {
    List<MenuDto> getAllMenus();
    List<Menu> getMenus(HttpServletRequest request);
    MenuDto getMenuDetails(long menuId);
    String menuReordered(List<MenuDto> menuDtoList, HttpServletRequest request);
    String updateMenuInfo(MenuDto menuDto, HttpServletRequest request);
    String deleteMenu(long menuId, HttpServletRequest request);

    /*-- UTILITY --*/
    int setMenuAuth(String userRole);
    void refineSortOrder();
}
