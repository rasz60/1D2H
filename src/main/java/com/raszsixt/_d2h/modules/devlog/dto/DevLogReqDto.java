package com.raszsixt._d2h.modules.devlog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DevLogReqDto {
    private String groupNo;
    private String itemNo;
    private String groupTitle;
    private String progress;

    private Long register;
    private Long updater;

    private Long targetItemId;
    private String targetItemType;
    private Long targetUserMgmtNo;

    public DevLogReqDto(String groupNo, String itemNo) {
        this.groupNo = groupNo;
        this.itemNo = itemNo;
    }

    public Long getLongTypeGroupNo() {
        return Long.parseLong(this.groupNo);
    }


    public Long getLongTypeItemNo() {
        return Long.parseLong(this.itemNo);
    }
}
