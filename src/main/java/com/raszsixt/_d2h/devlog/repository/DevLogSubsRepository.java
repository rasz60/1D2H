package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevLogSubsRepository extends JpaRepository<DevLogSubscribe, Long> {
    int countByItemIdAndItemSubsType(Long itemId, String itemSubsType);
    int countByItemIdAndItemSubsTypeAndItemSubsRegister(Long itemId, String itemSubsType, Long itemSubsRegister);
}
