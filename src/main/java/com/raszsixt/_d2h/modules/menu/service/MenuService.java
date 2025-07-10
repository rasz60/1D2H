package com.raszsixt._d2h.modules.menu.service;

import com.raszsixt._d2h.modules.menu.entity.Menu;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface MenuService {
    List<Menu> getMenus(HttpServletRequest request);
}
