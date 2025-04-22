package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevLogItemRepository extends JpaRepository<DevLogItem, Long> {
    List<DevLogItem> findByDeleteYnAndOpenYnAndGroupNo_GroupNoOrderByItemSortNoDesc(String deleteYn, String openYn, Long groupNo);
}
