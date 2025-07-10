package com.raszsixt._d2h.modules.devlog.repository;

import com.raszsixt._d2h.modules.devlog.entity.DevLogLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DevLogLikeRepository extends JpaRepository<DevLogLike, Long> {
    int countByItemIdAndItemType(Long itemId, String itemType);
    int countByItemIdAndItemTypeAndItemLikeUserNo(Long itemId, String itemType, Long itemLikeUserNo);
    Optional<DevLogLike> findByItemIdAndItemTypeAndItemLikeUserNo(Long itemId, String itemType, Long itemLikeUserNo);
    int deleteByItemId(Long itemId);
}
