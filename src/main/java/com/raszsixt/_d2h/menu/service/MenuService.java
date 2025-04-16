package com.raszsixt._d2h.menu.service;

import com.raszsixt._d2h.menu.entity.Menu;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface MenuService {
    List<Menu> getMenus(HttpServletRequest request);
}
