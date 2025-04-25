package com.raszsixt._d2h.devlog.controller;

import com.raszsixt._d2h.devlog.dto.DevLogReqDto;
import com.raszsixt._d2h.devlog.service.DevLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/itemList/{groupNo}")
    public ResponseEntity<?> getGroupList(@PathVariable(name = "groupNo") String groupNo, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.getItemListWithGroupNo(groupNo, request));
    }

    @PostMapping("/updateLikes")
    public ResponseEntity<?> updateLikes(@RequestBody DevLogReqDto devLogReqDto, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.updateLikes(devLogReqDto, request));
    }
}
