package com.raszsixt._d2h.modules.devlog.service;

import com.raszsixt._d2h.modules.devlog.dto.*;
import com.raszsixt._d2h.modules.devlog.entity.*;
import com.raszsixt._d2h.modules.devlog.repository.*;
import com.raszsixt._d2h.modules.user.dto.UserDto;
import com.raszsixt._d2h.modules.user.service.UserService;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DevLogServiceImpl implements DevLogService {
    private final DevLogGroupRepository devLogGroupRepository;
    private final DevLogItemRepository devLogitemRepository;
    private final DevLogLikeRepository devLogLikeRepository;
    private final DevLogSubsRepository devLogSubsRepository;
    private final DevLogVisitLogRepository devLogVisitLogRepository;
    private final DevLogItemLangRepository devLogItemLangRepository;
    private final DevLogLangRepository devLogLangRepository;
    private final UserService userService;

    public DevLogServiceImpl(DevLogGroupRepository devLogGroupRepository,
                             DevLogItemRepository devLogitemRepository,
                             DevLogLikeRepository devLogLikeRepository,
                             DevLogSubsRepository devLogSubsRepository,
                             DevLogVisitLogRepository devLogVisitLogRepository,
                             DevLogItemLangRepository devLogItemLangRepository,
                             DevLogLangRepository devLogLangRepository,
                             UserService userService) {
        this.devLogGroupRepository = devLogGroupRepository;
        this.devLogitemRepository = devLogitemRepository;
        this.devLogLikeRepository = devLogLikeRepository;
        this.devLogSubsRepository = devLogSubsRepository;
        this.devLogVisitLogRepository = devLogVisitLogRepository;
        this.devLogItemLangRepository = devLogItemLangRepository;
        this.devLogLangRepository = devLogLangRepository;
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
    public String groupSave(DevLogReqDto devLogReqDto, HttpServletRequest request) {
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);
        if ( loginInfo == null ) {
            throw new SecurityException("");
        }

        DevLogGroup group = new DevLogGroup();

        group.setGroupTitle(devLogReqDto.getGroupTitle());
        group.setGroupRegister(loginInfo.getUserMgmtNo());
        group.setGroupRegistDate(LocalDateTime.now());
        group.setOpenYn("Y");
        group.setDeleteYn("N");
        group.setProgress("Y");

        devLogGroupRepository.save(group);

        return "Success";
    }

    @Override
    public String updateGroupProgress(DevLogReqDto devLogReqDto, HttpServletRequest request) {
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);
        if ( loginInfo == null ) {
            throw new SecurityException("");
        }

        DevLogGroup group = devLogGroupRepository.findById(Long.parseLong(devLogReqDto.getGroupNo())).orElse(null);
        if ( group == null ) {
            throw new IllegalArgumentException("존재하지 않는 그룹입니다.");
        }

        group.setProgress(devLogReqDto.getProgress());
        group.setGroupUpdater(loginInfo.getUserMgmtNo());
        group.setGroupUpdateDate(LocalDateTime.now());
        devLogGroupRepository.save(group);

        return "Success";
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

            // 8. 사용 언어
            List<DevLogItemLang> langs = getItemLang(item);
            if ( langs != null ) {
                dto.setItemLangs(DevLogItemLangDto.of(langs));
            }

            // 9. dto list에 추가
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

    public List<DevLogItemLang> getItemLang(DevLogItem item) {
        return devLogItemLangRepository
                .findByItemNo_itemNoOrderByLangId(item.getItemNo());
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

    @Override
    @Transactional
    public DevLogItemDto itemDetails(DevLogReqDto devLogReqDto, HttpServletRequest request) {
        DevLogItemDto dto = null;
        
        // 1. item 조회
        DevLogItem item = devLogitemRepository.findByDeleteYnAndGroupNo_GroupNoAndItemNo("N", devLogReqDto.getLongTypeGroupNo(), devLogReqDto.getLongTypeItemNo()).orElse(null);
        
        // 2. 존재하는 item일 때
        if ( item != null ) {
            // 3. 로그인 정보
            UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

            // 4. dto로 전환
            dto = DevLogItemDto.of(item);

            // 5. register 조회
            String register = userService.findUserIdFromUserMgmtNo(item.getItemRegister());
            dto.setItemRegisterId(register);

            // 6. updater 조회
            String updater = userService.findUserIdFromUserMgmtNo(item.getItemUpdater());
            dto.setItemUpdaterId(updater);

            // 7. 좋아요, 조회수 조회
            devLogReqDto.setTargetItemId(item.getItemNo());
            dto.setLikeCnt(getLikeCnt(devLogReqDto));
            dto.setViewCnt(getViewCnt(devLogReqDto));

            // 8. 로그인 유저의 좋아요, 조회 여부 조회
            if (loginInfo != null) {
                dto.setLikeYn(getLikeYn(devLogReqDto));
                dto.setViewYn(getViewYn(devLogReqDto));
                dto.setEditYn(Objects.equals(loginInfo.getUserMgmtNo(), item.getItemRegister()));
                dto.setDeleteYn(Objects.equals(loginInfo.getUserMgmtNo(), item.getItemRegister()));
            }

            // 9. 사용 언어
            List<DevLogItemLang> langs = getItemLang(item);
            if (langs != null) {
                dto.setItemLangs(DevLogItemLangDto.of(langs));
            }

            // 10. 조회 로그
            int viewCnt = 0;
            if ( loginInfo != null ) {
                viewCnt = devLogVisitLogRepository.countByItemNoAndItemTypeAndUserMgmtNo(item.getItemNo(), "DLI", loginInfo.getUserMgmtNo());

                if (viewCnt == 0) {
                    DevLogVisitLog visitLog = new DevLogVisitLog();
                    visitLog.setItemNo(item.getItemNo());
                    visitLog.setItemType("DLI");
                    visitLog.setUserMgmtNo(loginInfo.getUserMgmtNo());
                    devLogVisitLogRepository.save(visitLog);
                }
            }
        }

        return dto;
    }

    @Override
    public List<DevLogLangDto> getLangList(HttpServletRequest request) {
        List<DevLogLang> langList = devLogLangRepository.findAll();

        return DevLogLangDto.of(langList);
    }

    @Override
    @Transactional
    public String itemSave(DevLogItemDto devLogItemDto,HttpServletRequest request) {
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);
        // 1. 로그인 되지 않았을 때 return
        if ( loginInfo == null ) {
            throw new SecurityException("");
        }

        // 2. groupNo, itemNo 추출
        Long groupNo = devLogItemDto.getGroupNo();
        Long itemNo = devLogItemDto.getItemNo();

        // 3. group 기본 데이터 조회
        DevLogGroup group = devLogGroupRepository.findById(groupNo).orElse(null);

        // 4. group이 존재하지 않을 때
        if ( group == null ) {
            throw new IllegalArgumentException("존재하지 않는 그룹입니다.");
        }

        // 5-1. itemNo가 null일 때 : 신규 작성
        DevLogItem item = null;
        if ( itemNo == null ) {
            
            // 5-1-1. DevLogItem 초기 설정
            item = DevLogItem.of(devLogItemDto);
            item.setItemRegistDate(LocalDateTime.now());
            item.setItemRegister(loginInfo.getUserMgmtNo());
            item.setItemSortNo(group.getItemList().size()+1L);
            item.setDeleteYn("N");
            item.setOpenYn("Y");
            item.setProgress("Y");

        }
        // 5-2. itemNo가 null이 아닐 때 : 수정
        else {
            // 5-2-1. item 기존 데이터 조회
            item = devLogitemRepository.findByDeleteYnAndGroupNo_GroupNoAndItemNo("N", groupNo, itemNo).orElse(null);

            // 5-2-2. item이 존재하지 않을 때
            if ( item == null ) {
                throw new RuntimeException("존재하지 않는 게시물입니다.");
            }

            // 5-2-5. 변경된 정보 저장
            item.setItemTitle(devLogItemDto.getItemTitle());
            item.setItemContents(devLogItemDto.getItemContents());
            item.setItemUpdater(loginInfo.getUserMgmtNo());
            item.setItemUpdateDate(LocalDateTime.now());

        }

        // 6. group 설정
        item.setGroupNo(group);
        devLogitemRepository.save(item);

        // 7. 사용 언어 초기화
        devLogItemLangRepository.deleteByItemNo_itemNo(itemNo);

        // 8. 사용 언어 다시 저장
        for (DevLogItemLangDto itemLangDto : devLogItemDto.getItemLangs()) {
            DevLogLang lang = devLogLangRepository.findById(itemLangDto.getLangId()).orElse(null);

            if (lang != null) {
                DevLogItemLang devLogItemLang = new DevLogItemLang();
                devLogItemLang.setItemNo(item);
                devLogItemLang.setLangId(lang);
                devLogItemLangRepository.save(devLogItemLang);
            }
        }

        return "success";
    }

    @Override
    @Transactional
    public String itemDelete(Long itemNo,HttpServletRequest request) {
        UserDto loginInfo = userService.findUserInfoFromHttpRequest(request);

        // 1. 로그인되지 않았을 때
        if ( loginInfo == null ) {
            throw new SecurityException("");
        }

        // 2. item 조회
        DevLogItem item = devLogitemRepository.findById(itemNo).orElse(null);
        if ( item != null ) {

            // 3. delete flag 변경, 업데이트 유저 정보 설정
            item.setDeleteYn("Y");
            item.setItemUpdater(loginInfo.getUserMgmtNo());
            item.setItemUpdateDate(LocalDateTime.now());
            devLogitemRepository.save(item);
        }

        return "success";
    }


}
