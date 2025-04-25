package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.dto.DevLogReqDto;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DevLogService {
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request);
    public List<DevLogItemDto> getItemListWithGroupNo(String groupNo, HttpServletRequest request);
    public String updateLikes(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException, SecurityException;
    public String updateSubs(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException, SecurityException;
    public DevLogReqDto setTargetInfo(DevLogReqDto devLogReqDto, HttpServletRequest request) throws SecurityException;
}
