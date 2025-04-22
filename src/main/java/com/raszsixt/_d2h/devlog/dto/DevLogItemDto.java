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
    private String itemRegisterNo;
    private String itemRegisterId;
    private LocalDateTime itemUpdateDate;
    private String itemUpdaterNo;
    private String itemUpdaterId;
    private String progress;
    private boolean openYn;
    private boolean deleteYn;

    public static DevLogItemDto of(DevLogItem devLogItem) {
        DevLogItemDto dto = new DevLogItemDto();

        return dto;
    }
}
