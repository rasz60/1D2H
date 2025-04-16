package com.raszsixt._d2h.menu.controller;

import com.raszsixt._d2h.menu.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;
    public MenuController (MenuService menuService) { this.menuService = menuService; }
    @GetMapping("/getMenus")
    public ResponseEntity<?> getMenus(HttpServletRequest request) {
        return ResponseEntity.ok(menuService.getMenus(request));
    }
}
