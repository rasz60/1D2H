package com.raszsixt._d2h.devlog.controller;

import com.raszsixt._d2h.devlog.service.DevLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dlog")
public class DevLogController {

    private DevLogService devLogService;

    public DevLogController (DevLogService devLogService) {
        this.devLogService = devLogService;
    }

    @GetMapping("/groupList")
    public ResponseEntity<?> getGroupList(HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.getGroupList(request));
    }
}
