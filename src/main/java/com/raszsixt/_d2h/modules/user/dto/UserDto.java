package com.raszsixt._d2h.modules.user.dto;

import com.raszsixt._d2h.modules.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userMgmtNo;
    private String userId;
    private String newUserPwd;
    private String userEmailId;
    private String userEmailDomain;
    private String userEmail;
    private String userPhone;
    private String userBirth;
    private String userNation;
    private String userZipCode;
    private String userAddr;
    private String userAddrDesc;

    private boolean alarmYn;
    private String newAccessToken;

    public static UserDto of (User user) {
        UserDto newDto = new UserDto();
        newDto.setUserMgmtNo(user.getUserMgmtNo());
        newDto.setUserId(user.getUserId());
        newDto.setUserEmail(user.getUserEmail());
        newDto.setUserEmailId(user.getUserEmail().split("@")[0]);
        newDto.setUserEmailDomain(user.getUserEmail().split("@")[1]);
        newDto.setUserPhone(user.getUserPhone());
        newDto.setUserZipCode(user.getUserZipCode());
        newDto.setUserAddr(user.getUserAddr());
        newDto.setUserAddrDesc(user.getUserAddrDesc());
        newDto.setAlarmYn("Y".equals(user.getAlramYn()));

        if ( user.getUserBirth() != null )
            newDto.setUserBirth(user.getUserBirth().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        return newDto;
    }

    public static List<UserDto> of (List<User> userList) {
        return userList.stream().map(user -> UserDto.of(user)).toList();
    }
}
