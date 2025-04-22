package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import com.raszsixt._d2h.devlog.repository.DevLogGroupRepository;
import com.raszsixt._d2h.devlog.repository.DevLogItemRepository;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DevLogServiceImpl implements DevLogService {
    private final DevLogGroupRepository devLogGroupRepository;
    private final DevLogItemRepository devLogitemRepository;
    private final UserRepository userRepository;

    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository, DevLogItemRepository devLogitemRepository, UserRepository userRepository) {
        this.devLogGroupRepository = devLogGroupRepository;
        this.devLogitemRepository = devLogitemRepository;
        this.userRepository = userRepository;
    }
    @Override
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request) {
        List<DevLogGroup> groups = devLogGroupRepository.findByDeleteYnAndOpenYnOrderByGroupNoDesc("N", "Y");
        return groups.stream().map(DevLogGroupDto::of).toList();
    }

    @Override
    public List<DevLogItemDto> getItemListWithGroupNo(String groupNo, HttpServletRequest request) {
        List<DevLogItem> items = devLogitemRepository.findByDeleteYnAndOpenYnAndGroupNo_GroupNoOrderByItemSortNoDesc("N", "Y", Long.parseLong(groupNo));

        return items.stream().map(item -> {
            DevLogItemDto dto = DevLogItemDto.of(item);
            Optional<User> exsist = userRepository.findByUserMgmtNo(item.getItemRegister());
            if ( exsist.isPresent() ) {
                User register = exsist.get();
                dto.setItemRegisterId(register.getUserId());
            }

            exsist = userRepository.findByUserMgmtNo(item.getItemUpdater());
            if ( exsist.isPresent() ) {
                User updater = exsist.get();
                dto.setItemRegisterId(updater.getUserId());
            }
            return dto;
        }).toList();
    }
}
