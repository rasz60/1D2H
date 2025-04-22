package com.raszsixt._d2h.devlog.dto;

import com.raszsixt._d2h.devlog.entity.DevLogItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DevLogItemDto {
    private Long itemNo;
    private Long groupNo;
    private Long itemSortNo;
    private String itemType;
    private String itemTitle;
    private LocalDateTime itemRegistDate;
    private Long itemRegisterNo;
    private String itemRegisterId;
    private LocalDateTime itemUpdateDate;
    private Long itemUpdaterNo;
    private String itemUpdaterId;
    private String progress;
    private boolean openYn;
    private boolean deleteYn;

    public static DevLogItemDto of(DevLogItem devLogItem) {
        DevLogItemDto dto = new DevLogItemDto();

        dto.setItemNo(devLogItem.getItemNo());
        dto.setGroupNo(devLogItem.getGroupNo().getGroupNo());
        dto.setItemSortNo(devLogItem.getItemSortNo());
        dto.setItemType(devLogItem.getItemType());
        dto.setItemTitle(devLogItem.getItemTitle());
        dto.setItemRegistDate(devLogItem.getItemRegistDate());
        dto.setItemRegisterNo(devLogItem.getItemRegister());
        dto.setItemUpdaterNo(devLogItem.getItemUpdater());
        dto.setProgress(devLogItem.getProgress());
        dto.setOpenYn("Y".equals(devLogItem.getOpenYn()));
        dto.setDeleteYn("Y".equals(devLogItem.getDeleteYn()));

        return dto;
    }
}
