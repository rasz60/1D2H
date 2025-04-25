package com.raszsixt._d2h.devlog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DevLogReqDto {
    private String groupNo;
    private String itemNo;

    private Long register;
    private Long updater;

    private Long targetItemId;
    private String targetItemType;
    private Long targetUserMgmtNo;
}
