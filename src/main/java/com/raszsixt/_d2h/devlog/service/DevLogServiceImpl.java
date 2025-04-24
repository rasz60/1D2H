package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import com.raszsixt._d2h.devlog.repository.DevLogGroupRepository;
import com.raszsixt._d2h.devlog.repository.DevLogItemRepository;
import com.raszsixt._d2h.devlog.repository.DevLogLikeRepository;
import com.raszsixt._d2h.devlog.repository.DevLogSubsRepository;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DevLogServiceImpl implements DevLogService {
    private final DevLogGroupRepository devLogGroupRepository;
    private final DevLogItemRepository devLogitemRepository;
    private final DevLogLikeRepository devLogLikeRepository;
    private final DevLogSubsRepository devLogSubsRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository, DevLogItemRepository devLogitemRepository, DevLogLikeRepository devLogLikeRepository, DevLogSubsRepository devLogSubsRepository,UserRepository userRepository, JwtUtil jwtUtil) {
        this.devLogGroupRepository = devLogGroupRepository;
        this.devLogitemRepository = devLogitemRepository;
        this.devLogLikeRepository = devLogLikeRepository;
        this.devLogSubsRepository = devLogSubsRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    @Override
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request) {

        List<DevLogGroup> groups = devLogGroupRepository.findByDeleteYnAndOpenYnOrderByGroupNoDesc("N", "Y");
        return groups.stream().map(group -> {
            DevLogGroupDto dto = DevLogGroupDto.of(group);
            Optional<User> exsist = userRepository.findByUserMgmtNo(group.getGroupRegister());
            if ( exsist.isPresent() ) {
                User register = exsist.get();
                dto.setGroupRegisterId(register.getUserId());
            }

            Long groupNo = group.getGroupNo();
            String itemType = "DLG";
            int likeCnt = devLogLikeRepository.countByItemIdAndItemLikeType(groupNo, itemType);
            dto.setLikeCnt(likeCnt);

            int subsCnt = devLogSubsRepository.countByItemIdAndItemSubsType(groupNo, itemType);
            dto.setSubsCnt(subsCnt);

            Map<String, Object> loginInfo = jwtUtil.getUserIdFromToken(request);
            if ( loginInfo.containsKey("userId") ) {
                exsist = userRepository.findByUserIdAndUserSignOutYn((String) loginInfo.get("userId"), "N");
                if ( exsist.isPresent() ) {
                    Long userMgmtNo = exsist.get().getUserMgmtNo();
                    int likeYn = devLogLikeRepository.countByItemIdAndItemLikeTypeAndItemLikeUserNo(groupNo, itemType, userMgmtNo);
                    dto.setLikeYn(likeYn > 0);
                    int subsYn = devLogSubsRepository.countByItemIdAndItemSubsTypeAndItemSubsRegister(groupNo, itemType, userMgmtNo);
                    dto.setSubsYn(subsYn > 0);
                }
            }

            return dto;
        }).toList();
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
