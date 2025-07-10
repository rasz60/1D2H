package com.raszsixt._d2h.modules.devlog.dto;

import com.raszsixt._d2h.modules.devlog.entity.DevLogLang;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DevLogLangDto {
    private Long langId;
    private Long langTypeId;
    private String langType;
    private String langTypeColor;
    private String langName;
    private String useYn;

    public static DevLogLangDto of(DevLogLang devLogLang) {
        DevLogLangDto dto = new DevLogLangDto();

        dto.setLangId(devLogLang.getLangId());
        dto.setLangTypeId(devLogLang.getLangTypeId().getLangTypeId());
        dto.setLangType(devLogLang.getLangTypeId().getLangType());
        dto.setLangTypeColor(devLogLang.getLangTypeId().getLangColor());
        dto.setLangName(devLogLang.getLangName());
        dto.setUseYn(devLogLang.getUseYn());

        return dto;
    }

    public static List<DevLogLangDto> of(List<DevLogLang> devLogLangList) {
        List<DevLogLangDto> dtos = new ArrayList<>();
        for ( DevLogLang lang : devLogLangList ) {
            dtos.add(DevLogLangDto.of(lang));
        }
        return dtos;
    }
}
