package com.raszsixt._d2h.common.base;

import com.raszsixt._d2h.common.utils.mapper.GenericMapper;
import com.raszsixt._d2h.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @ManyToOne
    @JoinColumn(name="REGISTER_ID")
    private User registerId;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registDate;
    @ManyToOne
    @JoinColumn(name="UPDATER_ID")
    private User updaterId;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updateDate;

    public static <D, E> E of(D dto, Class<E> entityClass) {
        return GenericMapper.toEntity(dto, entityClass);
    }
    public static <D, E> List<E> ofList(List<D> dtos, Class<E> entityClass) {
        return GenericMapper.toEntityList(dtos, entityClass);
    }
}
