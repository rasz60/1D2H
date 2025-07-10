package com.raszsixt._d2h.modules.devlog.repository;

import com.raszsixt._d2h.modules.devlog.entity.DevLogItemLang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevLogItemLangRepository extends JpaRepository<DevLogItemLang, Long> {
    List<DevLogItemLang> findByItemNo_itemNoOrderByLangId(Long itemNo);
    int deleteByItemNo_itemNo(Long itemNo);
}
