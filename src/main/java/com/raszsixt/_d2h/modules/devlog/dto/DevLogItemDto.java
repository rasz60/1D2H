package com.raszsixt._d2h.modules.devlog.dto;

import com.raszsixt._d2h.modules.devlog.entity.DevLogItem;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
public class DevLogItemDto {
    private Long itemNo;
    private Long groupNo;
    private Long itemSortNo;
    private String itemType;
    private String itemTitle;
    private String itemContents;
    private String itemRegistDate;
    private Long itemRegisterNo;
    private String itemRegisterId;
    private String itemUpdateDate;
    private Long itemUpdaterNo;
    private String itemUpdaterId;
    private String progress;

    private boolean openYn;
    private boolean editYn;
    private boolean deleteYn;
    private boolean likeYn;
    private boolean viewYn;

    private int likeCnt;
    private int viewCnt;

    private List<DevLogItemLangDto> itemLangs;

    public static DevLogItemDto of(DevLogItem devLogItem) {
        DevLogItemDto dto = new DevLogItemDto();

        dto.setItemNo(devLogItem.getItemNo());
        dto.setGroupNo(devLogItem.getGroupNo().getGroupNo());
        dto.setItemSortNo(devLogItem.getItemSortNo());
        dto.setItemType(devLogItem.getItemType());
        dto.setItemTitle(devLogItem.getItemTitle());
        dto.setItemContents(devLogItem.getItemContents());
        if (devLogItem.getItemRegistDate() != null ) {
            dto.setItemRegistDate(devLogItem.getItemRegistDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        }
        dto.setItemRegisterNo(devLogItem.getItemRegister());
        if (devLogItem.getItemUpdateDate() != null ) {
            dto.setItemUpdateDate(devLogItem.getItemUpdateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        }
        dto.setItemUpdaterNo(devLogItem.getItemUpdater());
        dto.setProgress(devLogItem.getProgress());
        dto.setOpenYn("Y".equals(devLogItem.getOpenYn()));
        dto.setDeleteYn("Y".equals(devLogItem.getDeleteYn()));

        return dto;
    }
}
