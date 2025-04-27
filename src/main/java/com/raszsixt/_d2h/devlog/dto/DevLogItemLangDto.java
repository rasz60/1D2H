package com.raszsixt._d2h.devlog.dto;

import com.raszsixt._d2h.devlog.entity.DevLogItemLang;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DevLogItemLangDto {
    private String langType;
    private String langTypeColor;
    private String langName;

    public static DevLogItemLangDto of(DevLogItemLang devLogItemLang) {
        DevLogItemLangDto dto = new DevLogItemLangDto();

        dto.setLangType(devLogItemLang.getLangId().getLangTypeId().getLangType());
        dto.setLangTypeColor(devLogItemLang.getLangId().getLangTypeId().getLangColor());
        dto.setLangName(devLogItemLang.getLangId().getLangName());

        return dto;
    }

    public static List<DevLogItemLangDto> of(List<DevLogItemLang> devLogItemLangList) {
        List<DevLogItemLangDto> dtos = new ArrayList<>();
        for ( DevLogItemLang itemLang : devLogItemLangList ) {
            DevLogItemLangDto dto = new DevLogItemLangDto();

            dto.setLangType(itemLang.getLangId().getLangTypeId().getLangType());
            dto.setLangTypeColor(itemLang.getLangId().getLangTypeId().getLangColor());
            dto.setLangName(itemLang.getLangId().getLangName());

            dtos.add(dto);
        }
        return dtos;
    }
}
