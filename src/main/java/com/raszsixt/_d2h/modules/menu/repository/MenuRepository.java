package com.raszsixt._d2h.modules.menu.repository;

import com.raszsixt._d2h.modules.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByMenuUseYnAndMenuAuthLessThanOrderByMenuSortOrder(String menuUseYn, Integer menuAuth);
    List<Menu> findAllByOrderByMenuSortOrder();
    Long countBy();

}
