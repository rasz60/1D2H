package com.raszsixt._d2h.menu.service;

import com.raszsixt._d2h.menu.entity.Menu;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface MenuService {
    List<Menu> getMenus(Principal principal);
}
