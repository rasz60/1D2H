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
    private String userRole;
    private String userRoleName;
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
    private String signUpDate;
    private String firstVisitDate;
    private String latestVisitDate;
    private String pwdUpdateDate;
    private String updateDate;
    private String userExpiredDate;
    private String userSignOutYn;
    private boolean alarmYn;
    private String newAccessToken;

    public static UserDto of (User user) {
        UserDto newDto = new UserDto();
        newDto.setUserMgmtNo(user.getUserMgmtNo());
        newDto.setUserId(user.getUserId());
        newDto.setUserRole(user.getUserRole());
        newDto.setUserRoleName( "ROLE_USER".equals(user.getUserRole()) ? "일반유저" : "ROLE_ADMIN".equals(user.getUserRole()) ? "관리자" : "" );
        newDto.setUserEmail(user.getUserEmail());
        newDto.setUserEmailId(user.getUserEmail().split("@")[0]);
        newDto.setUserEmailDomain(user.getUserEmail().split("@")[1]);
        newDto.setUserPhone(user.getUserPhone());
        newDto.setUserZipCode(user.getUserZipCode());
        newDto.setUserAddr(user.getUserAddr());
        newDto.setUserAddrDesc(user.getUserAddrDesc());
        newDto.setAlarmYn("Y".equals(user.getAlramYn()));
        newDto.setUserSignOutYn(user.getUserSignOutYn());

        if ( user.getUserBirth() != null )
            newDto.setUserBirth(user.getUserBirth().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        if ( user.getSignUpDate() != null )
            newDto.setSignUpDate(user.getSignUpDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        if ( user.getFirstVisitDate() != null )
            newDto.setFirstVisitDate(user.getFirstVisitDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        if ( user.getLatestVisitDate() != null )
            newDto.setLatestVisitDate(user.getLatestVisitDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        if ( user.getPwdUpdateDate() != null )
            newDto.setPwdUpdateDate(user.getPwdUpdateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        if ( user.getUpdateDate() != null )
            newDto.setUpdateDate(user.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        if ( user.getUserExpiredDate() != null )
            newDto.setUserExpiredDate(user.getUserExpiredDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss")));

        return newDto;
    }

    public static List<UserDto> of (List<User> userList) {
        return userList.stream().map(user -> UserDto.of(user)).toList();
    }
}
