package com.raszsixt._d2h.modules.menu.dto;

import com.raszsixt._d2h.common.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MenuDto extends BaseDto {
    private Long menuId;
    private String menuName;
    private String menuIcon;
    private String menuUrl;
    private Integer menuAuth;
    private String menuUseYn;
    private String menuTarget;
    private int menuSortOrder;
    private boolean auth;
}
