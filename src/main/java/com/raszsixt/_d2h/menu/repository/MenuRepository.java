package com.raszsixt._d2h.menu.repository;

import com.raszsixt._d2h.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByMenuUseYnAndMenuAuthLessThan(String menuUseYn, Integer menuAuth);
}
