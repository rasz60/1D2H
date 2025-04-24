package com.raszsixt._d2h.devlog.dto;

import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.devlog.entity.DevLogItem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class DevLogGroupDto {
    private Long groupNo;
    private String groupTitle;
    private LocalDateTime groupRegistDate;
    private Long groupRegisterNo;
    private String groupRegisterId;
    private LocalDateTime groupUpdateDate;
    private Long groupUpdateNo;
    private String groupUpdateId;

    private String progress;
    private boolean openYn;
    private boolean deleteYn;
    private boolean likeYn;
    private boolean subsYn;

    private int itemCnt;
    private int likeCnt;
    private int subsCnt;

    public static DevLogGroupDto of(DevLogGroup devLogGroup) {
        DevLogGroupDto dto = new DevLogGroupDto();

        dto.setGroupNo(devLogGroup.getGroupNo());
        dto.setGroupTitle(devLogGroup.getGroupTitle());
        dto.setGroupRegistDate(devLogGroup.getGroupRegistDate());
        dto.setGroupRegisterNo(devLogGroup.getGroupRegister());
        dto.setGroupUpdateDate(devLogGroup.getGroupUpdateDate());
        dto.setGroupUpdateNo(devLogGroup.getGroupUpdater());
        dto.setProgress(devLogGroup.getProgress());
        dto.setOpenYn("Y".equals(devLogGroup.getOpenYn()));
        dto.setDeleteYn("Y".equals(devLogGroup.getDeleteYn()));
        dto.setItemCnt(devLogGroup.getItemList().size());

        return dto;
    }
}
