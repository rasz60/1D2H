package com.raszsixt._d2h.devlog.repository;

import com.raszsixt._d2h.devlog.entity.DevLogLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevLogLikeRepository extends JpaRepository<DevLogLike, Long> {
    int countByItemIdAndItemLikeType(Long itemId, String itemLikeType);
    int countByItemIdAndItemLikeTypeAndItemLikeUserNo(Long itemId, String itemLikeType, Long itemLikeUserNo);
}
