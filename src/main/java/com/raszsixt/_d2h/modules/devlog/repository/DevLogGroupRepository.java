package com.raszsixt._d2h.modules.devlog.repository;

import com.raszsixt._d2h.modules.devlog.entity.DevLogGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevLogGroupRepository extends JpaRepository<DevLogGroup, Long> {
    List<DevLogGroup> findByDeleteYnAndOpenYnOrderByGroupNoDesc(String deleteYn, String openYn);
}
