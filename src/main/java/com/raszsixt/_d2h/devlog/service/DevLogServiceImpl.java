package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.dto.DevLogReqDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import com.raszsixt._d2h.devlog.entity.DevLogLike;
import com.raszsixt._d2h.devlog.repository.*;
import com.raszsixt._d2h.user.dto.UserDto;
import com.raszsixt._d2h.user.service.UserService;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DevLogServiceImpl implements DevLogService {
    private final DevLogGroupRepository devLogGroupRepository;
    private final DevLogItemRepository devLogitemRepository;
    private final DevLogLikeRepository devLogLikeRepository;
    private final DevLogSubsRepository devLogSubsRepository;
    private final DevLogVisitLogRepository devLogVisitLogRepository;
    private final UserService userService;

    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository,
                             DevLogItemRepository devLogitemRepository,
                             DevLogLikeRepository devLogLikeRepository,
                             DevLogSubsRepository devLogSubsRepository,
                             DevLogVisitLogRepository devLogVisitLogRepository,
                             UserService userService) {
        this.devLogGroupRepository = devLogGroupRepository;
        this.devLogitemRepository = devLogitemRepository;
        this.devLogLikeRepository = devLogLikeRepository;
        this.devLogSubsRepository = devLogSubsRepository;
        this.devLogVisitLogRepository = devLogVisitLogRepository;
        this.userService = userService;
    }
    @Override
    public List<DevLogGroupDto> getGroupList(HttpServletRequest request) {
        List<DevLogGroupDto> dtos = new ArrayList<>();

        // 1. 전체 그룹 조회
        List<DevLogGroup> groups = devLogGroupRepository.findByDeleteYnAndOpenYnOrderByGroupNoDesc("N", "Y");
        
        for ( int i = 0; i < groups.size(); i++ ) {
            // 2. dto로 전환
            DevLogGroup group = groups.get(i);
            DevLogGroupDto dto = DevLogGroupDto.of(group);
            
            // 3. register 조회
            String register = userService.findUserIdFromUserMgmtNo(group.getGroupRegister());
            dto.setGroupRegisterId(register);
            
            // 4. 좋아요, 구독 수 조회
            Long groupNo = group.getGroupNo();
            String itemType = "DLG";
            int likeCnt = devLogLikeRepository.countByItemIdAndItemType(groupNo, itemType);
            dto.setLikeCnt(likeCnt);

            int subsCnt = devLogSubsRepository.countByItemIdAndItemType(groupNo, itemType);
            dto.setSubsCnt(subsCnt);
            
            // 5. 로그인 유저의 좋아요, 구독 여부 조회
            UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);
            if ( loginInfo != null ) {
                int likeYn = devLogLikeRepository.countByItemIdAndItemTypeAndItemLikeUserNo(groupNo, itemType, loginInfo.getUserMgmtNo());
                dto.setLikeYn(likeYn > 0);
                int subsYn = devLogSubsRepository.countByItemIdAndItemTypeAndItemSubsRegister(groupNo, itemType, loginInfo.getUserMgmtNo());
                dto.setSubsYn(subsYn > 0);
            }
            // 6. dto list에 추가
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<DevLogItemDto> getItemListWithGroupNo(String groupNo, HttpServletRequest request) {
        List<DevLogItemDto> dtos = new ArrayList<>();
        
        // 1. 전체 아이템 조회
        List<DevLogItem> items = devLogitemRepository.findByDeleteYnAndOpenYnAndGroupNo_GroupNoOrderByItemSortNoDesc("N", "Y", Long.parseLong(groupNo));
        
        for ( int i = 0; i < items.size(); i++ ) {
            // 2. dto로 전환
            DevLogItem item = items.get(i);
            DevLogItemDto dto = DevLogItemDto.of(item);
            
            // 3. register 조회
            String register = userService.findUserIdFromUserMgmtNo(item.getItemRegister());
            dto.setItemRegisterId(register);
            
            // 4. updater 조회
            String updater = userService.findUserIdFromUserMgmtNo(item.getItemUpdater());
            dto.setItemUpdaterId(updater);
            
            // 5. 좋아요, 조회수 조회
            Long itemNo = item.getItemNo();
            String itemType = "DLI";
            int likeCnt = devLogLikeRepository.countByItemIdAndItemType(itemNo, itemType);
            dto.setLikeCnt(likeCnt);

            int viewCnt = devLogVisitLogRepository.countByItemNoAndItemType(itemNo, itemType);
            dto.setViewCnt(viewCnt);
            
            // 6. 로그인 유저의 좋아요, 조회 여부 조회
            UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);
            if ( loginInfo != null ) {
                int likeYn = devLogLikeRepository.countByItemIdAndItemTypeAndItemLikeUserNo(itemNo, itemType, loginInfo.getUserMgmtNo());
                dto.setLikeYn(likeYn > 0);
                int viewYn = devLogVisitLogRepository.countByItemNoAndItemTypeAndUserMgmtNo(itemNo, itemType, loginInfo.getUserMgmtNo());
                dto.setViewYn(viewYn > 0);
            }

            // 7. dto list에 추가
            dtos.add(dto);
        }

        return dtos;
    }

    public String updateLikes(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException {
            String rst = "";

            // 1. groupNo 가 비어있을 때
            if ( devLogReqDto.getGroupNo().isEmpty() ) {
                throw new IllegalArgumentException("EMPTY VALUE : GROUP_NO");
            }

            // 2. loginUserInfo 조회
            UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

            // 3. loginUser를 찾을 수 없을 때
            if ( loginInfo == null ) {
                throw new SecurityException("");
            }

            // 4. like Entity 생성
            Long itemId = 0L;
            String itemType = "";

            // 5. 값 binding

            if ( devLogReqDto.getItemNo().isEmpty() ) {
                itemId = Long.parseLong(devLogReqDto.getGroupNo());
                itemType = "DLG";
            } else {
                itemId = Long.parseLong(devLogReqDto.getItemNo());
                itemType = "DLI";
            }

            // 6. 좋아요 여부 조회
            Optional<DevLogLike> likeYn = devLogLikeRepository.findByItemIdAndItemTypeAndItemLikeUserNo(itemId, itemType, loginInfo.getUserMgmtNo());

            if (likeYn.isPresent()) {
                devLogLikeRepository.delete(likeYn.get());
            } else {
                DevLogLike like = new DevLogLike();
                like.setItemId(itemId);
                like.setItemType(itemType);
                like.setItemLikeUserNo(loginInfo.getUserMgmtNo());
                devLogLikeRepository.save(like);
            }

            return "SUCCESS";
    }

}
