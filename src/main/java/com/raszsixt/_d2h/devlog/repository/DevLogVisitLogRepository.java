package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogVisitLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevLogVisitLogRepository extends JpaRepository<DevLogVisitLog, Long> {
    int countByItemNoAndItemType(Long itemNo, String itemType);

    int countByItemNoAndItemTypeAndUserMgmtNo(Long itemNo, String itemType, Long userMgmtNo);
}
