package com.raszsixt._d2h.modules.devlog.service;

import com.raszsixt._d2h.modules.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.modules.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.modules.devlog.dto.DevLogLangDto;
import com.raszsixt._d2h.modules.devlog.dto.DevLogReqDto;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DevLogService {
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request);
    public String groupSave(DevLogReqDto devLogReqDto, HttpServletRequest request);
    public String updateGroupProgress(DevLogReqDto devLogReqDto, HttpServletRequest request);
    public List<DevLogItemDto> getItemListWithGroupNo(String groupNo, HttpServletRequest request);
    public String updateLikes(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException, SecurityException;
    public String updateSubs(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException, SecurityException;
    public DevLogReqDto setTargetInfo(DevLogReqDto devLogReqDto, HttpServletRequest request) throws SecurityException;
    public DevLogItemDto itemDetails(DevLogReqDto devLogReqDto, HttpServletRequest request);
    public List<DevLogLangDto> getLangList(HttpServletRequest request);
    public String itemSave(DevLogItemDto devLogItemDto,HttpServletRequest request);
    public String itemDelete(Long itemNo,HttpServletRequest request);
}
