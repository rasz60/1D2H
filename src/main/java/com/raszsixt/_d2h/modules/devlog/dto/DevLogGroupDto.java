package com.raszsixt._d2h.modules.devlog.dto;

import com.raszsixt._d2h.modules.devlog.entity.DevLogGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class DevLogGroupDto {
    private Long groupNo;
    private String groupTitle;
    private String groupRegistDate;
    private Long groupRegisterNo;
    private String groupRegisterId;
    private String groupUpdateDate;
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
        if (devLogGroup.getGroupRegistDate() != null ) {
            dto.setGroupRegistDate(devLogGroup.getGroupRegistDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        }
        dto.setGroupRegisterNo(devLogGroup.getGroupRegister());
        if (devLogGroup.getGroupUpdateDate() != null ) {
            dto.setGroupUpdateDate(devLogGroup.getGroupUpdateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        }
        dto.setGroupUpdateNo(devLogGroup.getGroupUpdater());
        dto.setProgress(devLogGroup.getProgress());
        dto.setOpenYn("Y".equals(devLogGroup.getOpenYn()));
        dto.setDeleteYn("Y".equals(devLogGroup.getDeleteYn()));
        dto.setItemCnt(devLogGroup.getItemList().size());

        return dto;
    }
}
