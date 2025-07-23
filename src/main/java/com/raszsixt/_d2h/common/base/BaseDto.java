package com.raszsixt._d2h.common.base;

import com.raszsixt._d2h.common.utils.mapper.GenericMapper;
import com.raszsixt._d2h.modules.user.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
public class BaseDto {
    private String registDate;
    private String registerId;
    private Long registerNo;
    private String updateDate;
    private String updaterId;
    private Long updaterNo;
    public static <D, E> D of (E entity, Class<D> dtoClass) {
       return GenericMapper.toDto(entity, dtoClass);
    }
    public static <D, E> List<D> ofList(List<E> entities, Class<D> dtoClass) {
        return GenericMapper.toDtoList(entities, dtoClass);
    }

}
