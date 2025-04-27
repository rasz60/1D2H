package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogLang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevLogLangRepository extends JpaRepository<DevLogLang, Long> {
    List<DevLogLang> findByLangId(Long langId);
}
