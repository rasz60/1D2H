package com.raszsixt._d2h.modules.devlog.dto;

import com.raszsixt._d2h.modules.devlog.entity.DevLogItemLang;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DevLogItemLangDto {
    private Long itemLangId;
    private Long langTypeId;
    private String langType;
    private String langTypeColor;
    private Long langId;
    private String langName;

    public static DevLogItemLangDto of(DevLogItemLang devLogItemLang) {
        DevLogItemLangDto dto = new DevLogItemLangDto();
        dto.setItemLangId(devLogItemLang.getItemLangId());
        dto.setLangTypeId(devLogItemLang.getLangId().getLangTypeId().getLangTypeId());
        dto.setLangType(devLogItemLang.getLangId().getLangTypeId().getLangType());
        dto.setLangTypeColor(devLogItemLang.getLangId().getLangTypeId().getLangColor());
        dto.setLangId(devLogItemLang.getLangId().getLangId());
        dto.setLangName(devLogItemLang.getLangId().getLangName());

        return dto;
    }

    public static List<DevLogItemLangDto> of(List<DevLogItemLang> devLogItemLangList) {
        List<DevLogItemLangDto> dtos = new ArrayList<>();
        for ( DevLogItemLang itemLang : devLogItemLangList ) {
            dtos.add(DevLogItemLangDto.of(itemLang));
        }
        return dtos;
    }
}
