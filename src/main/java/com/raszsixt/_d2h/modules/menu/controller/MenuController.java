package com.raszsixt._d2h.modules.menu.controller;

import com.raszsixt._d2h.modules.menu.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;
    @GetMapping("/getMenus")
    public ResponseEntity<?> getMenus(HttpServletRequest request) {
        return ResponseEntity.ok(menuService.getMenus(request));
    }
}
