package com.raszsixt._d2h.modules.menu.controller;

import com.raszsixt._d2h.modules.menu.dto.MenuDto;
import com.raszsixt._d2h.modules.menu.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;
    @GetMapping("/getMenus")
    public ResponseEntity<?> getMenus(HttpServletRequest request) {
        return ResponseEntity.ok(menuService.getMenus(request));
    }
    @GetMapping("/getAllMenus")
    public ResponseEntity<?> getAllMenus(HttpServletRequest request) {
        return ResponseEntity.ok(menuService.getAllMenus());
    }
    @GetMapping("/getMenuDetails/{menuId}")
    public ResponseEntity<?> getMenuDetails(@PathVariable(name = "menuId") long menuId) {
        return ResponseEntity.ok(menuService.getMenuDetails(menuId));
    }
    @PostMapping("/menuReordered")
    public ResponseEntity<?> menuReordered(@RequestBody List<MenuDto> menuDtoList, HttpServletRequest request) {
        return ResponseEntity.ok(menuService.menuReordered(menuDtoList, request));
    }
    @PostMapping("/updateMenuInfo")
    public ResponseEntity<?> updateMenuInfo(@RequestBody MenuDto menuDto, HttpServletRequest request) {
        return ResponseEntity.ok(menuService.updateMenuInfo(menuDto, request));
    }
}
