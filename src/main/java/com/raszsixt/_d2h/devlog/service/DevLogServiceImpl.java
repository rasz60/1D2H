package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.repository.DevLogGroupRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DevLogServiceImpl implements DevLogService {
    private DevLogGroupRepository devLogGroupRepository;
    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository) {
        this.devLogGroupRepository = devLogGroupRepository;
    }
    @Override
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request) {
        List<DevLogGroup> groups = devLogGroupRepository.findByDeleteYnAndOpenYnOrderByGroupNo("N", "Y");
        return groups.stream().map((group) -> DevLogGroupDto.of(group)).toList();
    }
}
