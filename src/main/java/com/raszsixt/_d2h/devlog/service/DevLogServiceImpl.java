package com.raszsixt._d2h.devlog.service;

import com.raszsixt._d2h.devlog.dto.DevLogGroupDto;
import com.raszsixt._d2h.devlog.dto.DevLogItemDto;
import com.raszsixt._d2h.devlog.dto.DevLogReqDto;
import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import com.raszsixt._d2h.devlog.entity.DevLogLike;
import com.raszsixt._d2h.devlog.entity.DevLogSubscribe;
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

        // 2. 로그인 유저 정보 조회
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

        DevLogReqDto devLogReqDto = new DevLogReqDto();
        devLogReqDto.setTargetItemType("DLG");
        if ( loginInfo != null ) {
            devLogReqDto.setTargetUserMgmtNo(loginInfo.getUserMgmtNo());
        }

        for ( DevLogGroup group : groups ) {
            // 3. dto로 전환
            DevLogGroupDto dto = DevLogGroupDto.of(group);
            
            // 4. register 조회
            String register = userService.findUserIdFromUserMgmtNo(group.getGroupRegister());
            dto.setGroupRegisterId(register);
            
            // 5. 좋아요, 구독 수 조회
            devLogReqDto.setTargetItemId(group.getGroupNo());
            dto.setLikeCnt(getLikeCnt(devLogReqDto));
            dto.setSubsCnt(getSubsCnt(devLogReqDto));
            
            // 6. 로그인 유저의 좋아요, 구독 여부 조회
            if ( loginInfo != null ) {
                dto.setLikeYn(getLikeYn(devLogReqDto));
                dto.setSubsYn(getSubsYn(devLogReqDto));
            }

            // 7. dto list에 추가
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<DevLogItemDto> getItemListWithGroupNo(String groupNo, HttpServletRequest request) {
        List<DevLogItemDto> dtos = new ArrayList<>();
        
        // 1. 전체 아이템 조회
        List<DevLogItem> items = devLogitemRepository.findByDeleteYnAndOpenYnAndGroupNo_GroupNoOrderByItemSortNoDesc("N", "Y", Long.parseLong(groupNo));

        // 2. 로그인 정보
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

        DevLogReqDto devLogReqDto = new DevLogReqDto();
        devLogReqDto.setTargetItemType("DLI");
        if ( loginInfo != null ) {
            devLogReqDto.setTargetUserMgmtNo(loginInfo.getUserMgmtNo());
        }
        for ( DevLogItem item : items ) {
            // 3. dto로 전환
            DevLogItemDto dto = DevLogItemDto.of(item);
            
            // 4. register 조회
            String register = userService.findUserIdFromUserMgmtNo(item.getItemRegister());
            dto.setItemRegisterId(register);
            
            // 5. updater 조회
            String updater = userService.findUserIdFromUserMgmtNo(item.getItemUpdater());
            dto.setItemUpdaterId(updater);
            
            // 6. 좋아요, 조회수 조회
            devLogReqDto.setTargetItemId(item.getItemNo());
            dto.setLikeCnt(getLikeCnt(devLogReqDto));
            dto.setViewCnt(getViewCnt(devLogReqDto));
            
            // 7. 로그인 유저의 좋아요, 조회 여부 조회
            if ( loginInfo != null ) {
                dto.setLikeYn(getLikeYn(devLogReqDto));
                dto.setViewYn(getViewYn(devLogReqDto));
            }

            // 8. dto list에 추가
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public String updateLikes(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException {
            String rst = "";

            // 1. groupNo 가 비어있을 때
            if ( devLogReqDto.getGroupNo().isEmpty() ) {
                throw new IllegalArgumentException("EMPTY VALUE : GROUP_NO");
            }

            // 2. targetInfo setting
            devLogReqDto = setTargetInfo(devLogReqDto, request);

            // 3. 좋아요 여부 조회
            DevLogLike like = getLike(devLogReqDto);

            // 4. 이미 좋아요 했을 때 삭제, 없을 때 등록
            if (like != null) {
                devLogLikeRepository.delete(like);
            } else {
                like = new DevLogLike();
                like.setItemId(devLogReqDto.getTargetItemId());
                like.setItemType(devLogReqDto.getTargetItemType());
                like.setItemLikeUserNo(devLogReqDto.getTargetUserMgmtNo());
                devLogLikeRepository.save(like);
            }
            return "SUCCESS";
    }

    @Override
    public String updateSubs(DevLogReqDto devLogReqDto, HttpServletRequest request) throws IllegalArgumentException {
        String rst = "";

        // 1. groupNo 가 비어있을 때
        if ( devLogReqDto.getGroupNo().isEmpty() ) {
            throw new IllegalArgumentException("EMPTY VALUE : GROUP_NO");
        }

        // 2. targetInfo setting
        devLogReqDto = setTargetInfo(devLogReqDto, request);

        // 3. 구독 여부 조회
        DevLogSubscribe subs = getSubs(devLogReqDto);

        // 4. 이미 구독 했을 때 삭제, 없을 때 등록
        if (subs != null) {
            devLogSubsRepository.delete(subs);
        } else {
            subs = new DevLogSubscribe();
            subs.setItemId(devLogReqDto.getTargetItemId());
            subs.setItemType(devLogReqDto.getTargetItemType());
            subs.setItemSubsRegister(devLogReqDto.getTargetUserMgmtNo());
            devLogSubsRepository.save(subs);
        }
        return "SUCCESS";
    }

    public int getLikeCnt(DevLogReqDto devLogReqDto) {
        return devLogLikeRepository
                .countByItemIdAndItemType(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType());
    }

    public int getSubsCnt(DevLogReqDto devLogReqDto) {
        return devLogSubsRepository
                .countByItemIdAndItemType(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType());
    }

    public int getViewCnt(DevLogReqDto devLogReqDto) {
        return devLogVisitLogRepository
                .countByItemNoAndItemType(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType());
    }

    public boolean getLikeYn(DevLogReqDto devLogReqDto) {
        return devLogLikeRepository
                .countByItemIdAndItemTypeAndItemLikeUserNo(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType(),
                        devLogReqDto.getTargetUserMgmtNo()) > 0;
    }

    public boolean getSubsYn(DevLogReqDto devLogReqDto) {
        return devLogSubsRepository
                .countByItemIdAndItemTypeAndItemSubsRegister(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType(),
                        devLogReqDto.getTargetUserMgmtNo()) > 0;
    }

    public boolean getViewYn(DevLogReqDto devLogReqDto) {
        return devLogVisitLogRepository
                .countByItemNoAndItemTypeAndUserMgmtNo(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType(),
                        devLogReqDto.getTargetUserMgmtNo()) > 0;
    }

    public DevLogLike getLike(DevLogReqDto devLogReqDto) {
        return devLogLikeRepository
                .findByItemIdAndItemTypeAndItemLikeUserNo(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType(),
                        devLogReqDto.getTargetUserMgmtNo()).orElse(null);
    }

    public DevLogSubscribe getSubs(DevLogReqDto devLogReqDto) {
        return devLogSubsRepository
                .findByItemIdAndItemTypeAndItemSubsRegister(
                        devLogReqDto.getTargetItemId(),
                        devLogReqDto.getTargetItemType(),
                        devLogReqDto.getTargetUserMgmtNo()).orElse(null);
    }

    @Override
    public DevLogReqDto setTargetInfo(DevLogReqDto devLogReqDto, HttpServletRequest request) throws SecurityException {
        // 1. loginUserInfo 조회
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

        // 2. loginUser를 찾을 수 없을 때
        if ( loginInfo == null ) {
            throw new SecurityException("");
        }

        // 3. 값 binding
        devLogReqDto.setTargetUserMgmtNo(loginInfo.getUserMgmtNo());
        if ( devLogReqDto.getItemNo() == null ) {
            devLogReqDto.setTargetItemId(Long.parseLong(devLogReqDto.getGroupNo()));
            devLogReqDto.setTargetItemType("DLG");
        } else {
            devLogReqDto.setTargetItemId(Long.parseLong(devLogReqDto.getItemNo()));
            devLogReqDto.setTargetItemType("DLI");
        }

        return devLogReqDto;
    }

}
