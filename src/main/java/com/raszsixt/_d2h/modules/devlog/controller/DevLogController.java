package com.raszsixt._d2h.modules.devlog.controller;

import com.raszsixt._d2h.modules.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.modules.devlog.dto.DevLogReqDto;
import com.raszsixt._d2h.modules.devlog.service.DevLogService;
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

    @PostMapping("/updateSubs")
    public ResponseEntity<?> updateSubs(@RequestBody DevLogReqDto devLogReqDto, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.updateSubs(devLogReqDto, request));
    }

    @GetMapping("/itemDetails/{groupNo}/{itemNo}")
    public ResponseEntity<?> itemDetails(@PathVariable(name = "groupNo") String groupNo, @PathVariable(name = "itemNo") String itemNo, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.itemDetails(new DevLogReqDto(groupNo, itemNo), request));
    }

    @GetMapping("/langList")
    public ResponseEntity<?> getLangList(HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.getLangList(request));
    }

    @PostMapping("/itemSave")
    public ResponseEntity<?> itemSave(@RequestBody DevLogItemDto devLogItemDto, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.itemSave(devLogItemDto, request));
    }

    @DeleteMapping("/itemDelete/{itemNo}")
    public ResponseEntity<?> itemDelete(@PathVariable(name="itemNo") Long itemNo, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.itemDelete(itemNo, request));
    }

    @PostMapping("/groupSave")
    public ResponseEntity<?> groupSave(@RequestBody DevLogReqDto devLogReqDto, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.groupSave(devLogReqDto, request));
    }

    @PostMapping("/updateGroupProgress")
    public ResponseEntity<?> updateGroupProgress(@RequestBody DevLogReqDto devLogReqDto, HttpServletRequest request) {
        return ResponseEntity.ok(devLogService.updateGroupProgress(devLogReqDto, request));
    }
}
