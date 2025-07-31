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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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

        boolean updateFlag = true;

        if ( menuDto.getMenuId() <= 0 ) {
            updateFlag = false;
            menuDto.setMenuId(null);
            menuDto.setRegisterNo(userDto.getUserMgmtNo());
            menuDto.setRegistDate(updateDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            menuDto.setMenuSortOrder(9999);
        }
        menuDto.setUpdaterNo(userDto.getUserMgmtNo());
        menuDto.setUpdateDate(updateDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        menuDto.setMenuSortOrder(menuRepository.countBy().intValue() + 1);
        menuRepository.save(Menu.of(menuDto, Menu.class));

        refineSortOrder();

        res = updateFlag ? "메뉴 정보 수정에 성공했습니다." : "메뉴 등록에 성공했습니다.";
        return res;
    }

    public String deleteMenu(long menuId, HttpServletRequest request) {
        String res = "";
        try {
            menuRepository.deleteById(menuId);
            refineSortOrder();
            res = "메뉴 삭제에 성공했습니다.";
        } catch (Exception e) {
            log.error(e.getMessage());
            res = "일시적인 오류로 메뉴 삭제에 실패했습니다.";
        }
        return res;
    }

    /*-- UTILITY --*/

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
    @Transactional
    public void refineSortOrder() {
        List<Menu> menuList = menuRepository.findAllByOrderByMenuSortOrder();
        for ( int i = 0; i < menuList.size(); i++ ) {
            Menu menu = menuList.get(i);
            menu.setMenuSortOrder(i + 1);
        };
    }
}
