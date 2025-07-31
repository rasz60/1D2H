package com.raszsixt._d2h.common.utils.mapper;

import com.raszsixt._d2h.common.base.BaseDto;
import com.raszsixt._d2h.common.base.BaseEntity;
import com.raszsixt._d2h.modules.user.entity.User;
import com.raszsixt._d2h.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
public class GenericMapper {
    public static UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        GenericMapper.userRepository = userRepository;
    }
    public static <D, E> D toDto(E entity, Class<D> dtoClass) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(entity, dto);

            if ( dto instanceof BaseDto baseDto && entity instanceof BaseEntity baseEntity ) {
                baseDto.setRegisterId(baseEntity.getRegisterId().getUserId());
                baseDto.setRegisterNo(baseEntity.getRegisterId().getUserMgmtNo());
                baseDto.setRegistDate(baseEntity.getRegistDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")));
                baseDto.setUpdaterId(baseEntity.getUpdaterId().getUserId());
                baseDto.setUpdaterNo(baseEntity.getUpdaterId().getUserMgmtNo());
                baseDto.setUpdateDate(baseEntity.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")));
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Dto 변환 실패", e);
        }
    }
    public static <D, E> E toEntity(D dto, Class<E> entityClass) {
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(dto, entity);

            if ( dto instanceof BaseDto baseDto && entity instanceof BaseEntity baseEntity ) {
                baseEntity.setRegisterId(getUserEntity(baseDto.getRegisterNo()));
                baseEntity.setUpdaterId(getUserEntity(baseDto.getUpdaterNo()));

                baseEntity.setRegistDate(parseDateTimeStr(baseDto.getRegistDate()));
                baseEntity.setUpdateDate(parseDateTimeStr(baseDto.getUpdateDate()));
            }

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Entity 변환 실패", e);
        }
    }
    public static <D, E> List<D> toDtoList(List<E> entities, Class<D> dtoClass) {
        return entities.stream().map((entity) -> toDto(entity, dtoClass)).toList();
    }
    public static <D, E> List<E> toEntityList(List<D> dtos, Class<E> entityClass) {
        return dtos.stream().map((dto) -> toEntity(dto, entityClass)).toList();
    }
    public static User getUserEntity(Long userMgmtNo) {
        return userRepository.findById(userMgmtNo).orElse(null);
    }
    public static LocalDateTime parseDateTimeStr(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
    }
}
