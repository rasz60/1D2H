package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevLogItemRepository extends JpaRepository<DevLogItem, Long> {
    List<DevLogItem> findByDeleteYnAndOpenYnAndGroupNo_GroupNoOrderByItemSortNoDesc(String deleteYn, String openYn, Long groupNo);

    Optional<DevLogItem> findByDeleteYnAndGroupNo_GroupNoAndItemNo(String deleteYn, Long groupNo, Long itemNo);
}
