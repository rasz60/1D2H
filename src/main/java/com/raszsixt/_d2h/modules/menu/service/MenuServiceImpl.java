package com.raszsixt._d2h.modules.menu.service;

import com.raszsixt._d2h.common.utils.mapper.GenericMapper;
import com.raszsixt._d2h.modules.devlog.entity.DevLogGroup;
import com.raszsixt._d2h.modules.menu.dto.MenuDto;
import com.raszsixt._d2h.modules.menu.entity.Menu;
import com.raszsixt._d2h.modules.menu.repository.MenuRepository;
import com.raszsixt._d2h.modules.user.dto.UserDto;
import com.raszsixt._d2h.modules.user.repository.UserRepository;
import com.raszsixt._d2h.security.JwtUtil;
import com.raszsixt._d2h.modules.user.service.UserService;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
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

        return menuRepository.findByMenuUseYnAndMenuAuthLessThanOrderByMenuSortOrder("Y", setMenuAuth(userRole));
    }

    @Override
    public List<MenuDto> getAllMenus() {
        return MenuDto.ofList(menuRepository.findAllByOrderByMenuSortOrder(), MenuDto.class);
    }

    @Override
    public MenuDto getMenuDetails(long menuId) {
        Optional<Menu> opMenu = menuRepository.findById(menuId);
        return opMenu.map(menu -> MenuDto.of(menu, MenuDto.class)).orElse(null);
    }

    @Override
    public String menuReordered(List<MenuDto> menuDtoList, HttpServletRequest request) {
        String res = "";
        UserDto userDto = userService.findUserInfoFromHttpRequest(request);
        LocalDateTime updateDateTime = LocalDateTime.now();
        for (MenuDto dto : menuDtoList) {
            Menu menu = menuRepository.findById(dto.getMenuId()).orElseThrow();
            menu.setUpdaterId(userRepository.findById(userDto.getUserMgmtNo()).orElse(null));
            menu.setUpdateDate(updateDateTime);
            menu.setMenuSortOrder(dto.getMenuSortOrder());
            menuRepository.save(menu);
        }

        res = "순서 변경 성공";
        return res;
    }

    @Override
    public String updateMenuInfo(MenuDto menuDto, HttpServletRequest request) {
        String res = "";
        UserDto userDto = userService.findUserInfoFromHttpRequest(request);
        LocalDateTime updateDateTime = LocalDateTime.now();

        boolean updateFlag = menuDto.getMenuId() >= 0;

        if ( menuDto.getMenuId() < 0 ) {
            menuDto.setMenuId(null);
        }
        menuDto.setUpdaterNo(userDto.getUserMgmtNo());
        menuDto.setUpdateDate(updateDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

        menuRepository.save(Menu.of(menuDto, Menu.class));

        res = updateFlag ? "메뉴 정보 수정에 성공했습니다." : "메뉴 등록에 성공했습니다.";
        return res;
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
