package com.raszsixt._d2h.menu.service;

import com.raszsixt._d2h.menu.entity.Menu;
import com.raszsixt._d2h.menu.repository.MenuRepository;
import com.raszsixt._d2h.user.entity.User;
import com.raszsixt._d2h.user.service.UserService;
import com.raszsixt._d2h.user.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final UserService userService;

    public MenuServiceImpl(MenuRepository menuRepository, UserService userService) {
        this.menuRepository = menuRepository;
        this.userService = userService;
    }

    @Override
    public List<Menu> getMenus(Principal principal) {
        String userRole = "guest";
        if ( principal != null )
            userRole = userService.getLoginUserRole(principal.getName());

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
