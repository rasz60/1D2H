package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import com.raszsixt._d2h.devlog.repository.*;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DevLogServiceImpl implements DevLogService {
    private final DevLogGroupRepository devLogGroupRepository;
    private final DevLogItemRepository devLogitemRepository;
    private final DevLogLikeRepository devLogLikeRepository;
    private final DevLogSubsRepository devLogSubsRepository;
    private final DevLogVisitLogRepository devLogVisitLogRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository,
                             DevLogItemRepository devLogitemRepository,
                             DevLogLikeRepository devLogLikeRepository,
                             DevLogSubsRepository devLogSubsRepository,
                             DevLogVisitLogRepository devLogVisitLogRepository,
                             UserRepository userRepository,
                             JwtUtil jwtUtil) {
        this.devLogGroupRepository = devLogGroupRepository;
        this.devLogitemRepository = devLogitemRepository;
        this.devLogLikeRepository = devLogLikeRepository;
        this.devLogSubsRepository = devLogSubsRepository;
        this.devLogVisitLogRepository = devLogVisitLogRepository;
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
            int likeCnt = devLogLikeRepository.countByItemIdAndItemType(groupNo, itemType);
            dto.setLikeCnt(likeCnt);

            int subsCnt = devLogSubsRepository.countByItemIdAndItemType(groupNo, itemType);
            dto.setSubsCnt(subsCnt);

            Map<String, Object> loginInfo = jwtUtil.getUserIdFromToken(request);
            if ( loginInfo.containsKey("userId") ) {
                exsist = userRepository.findByUserIdAndUserSignOutYn((String) loginInfo.get("userId"), "N");
                if ( exsist.isPresent() ) {
                    Long userMgmtNo = exsist.get().getUserMgmtNo();
                    int likeYn = devLogLikeRepository.countByItemIdAndItemTypeAndItemLikeUserNo(groupNo, itemType, userMgmtNo);
                    dto.setLikeYn(likeYn > 0);
                    int subsYn = devLogSubsRepository.countByItemIdAndItemTypeAndItemSubsRegister(groupNo, itemType, userMgmtNo);
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

            Long itemNo = item.getItemNo();
            String itemType = "DLI";
            int likeCnt = devLogLikeRepository.countByItemIdAndItemType(itemNo, itemType);
            dto.setLikeCnt(likeCnt);

            int viewCnt = devLogVisitLogRepository.countByItemNoAndItemType(itemNo, itemType);
            dto.setViewCnt(viewCnt);

            Map<String, Object> loginInfo = jwtUtil.getUserIdFromToken(request);
            if ( loginInfo.containsKey("userId") ) {
                exsist = userRepository.findByUserIdAndUserSignOutYn((String) loginInfo.get("userId"), "N");
                if ( exsist.isPresent() ) {
                    Long userMgmtNo = exsist.get().getUserMgmtNo();
                    int likeYn = devLogLikeRepository.countByItemIdAndItemTypeAndItemLikeUserNo(itemNo, itemType, userMgmtNo);
                    dto.setLikeYn(likeYn > 0);
                    int viewYn = devLogVisitLogRepository.countByItemNoAndItemTypeAndUserMgmtNo(itemNo, itemType, userMgmtNo);
                    dto.setViewYn(viewYn > 0);
                }
            }
            return dto;
        }).toList();
    }
}
