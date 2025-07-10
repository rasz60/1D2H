package com.raszsixt._d2h.modules.menu.service;

import com.raszsixt._d2h.modules.menu.entity.Menu;
import com.raszsixt._d2h.modules.menu.repository.MenuRepository;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.modules.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    public MenuServiceImpl(MenuRepository menuRepository, UserService userService, JwtUtil jwtUtil) {
        this.menuRepository = menuRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<Menu> getMenus(HttpServletRequest request) {
        String userId = null;
        String token = jwtUtil.resolveAccessToken(request);

        if ( token != null ) {
            userId = jwtUtil.getUserIdFromToken(token);
        }
        String userRole = "guest";

        if ( userId != null ) {
            userRole = userService.getLoginUserRole(userId);
        }

        return menuRepository.findByMenuUseYnAndMenuAuthLessThan("Y", setMenuAuth(userRole));
    }

    public int setMenuAuth(String userRole) {
        int menuAuth = 0;

        if ( "guest".equals(userRole) ) {
            menuAuth = 1;
        } else if ( "ROLE_USER".equals(userRole) ) {
            menuAuth = 2;
        } else if ( "ROLE_ADMIN".equals(userRole) ) {
            menuAuth = 3;
        }

        return menuAuth;
    }
}
