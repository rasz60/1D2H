package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface DevLogGroupRepository extends JpaRepository<DevLogGroup, Long> {
    List<DevLogGroup> findByDeleteYnAndOpenYnOrderByGroupNo(String deleteYn, String openYn);
}
