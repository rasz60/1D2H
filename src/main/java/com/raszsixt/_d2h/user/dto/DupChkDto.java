package com.raszsixt._d2h.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DupChkDto {
    private String userId;
    private String userEmail;
    private String userPhone;
    private String dupChkType;

    private boolean userIdDupChk;
    private boolean userEmailDupChk;
    private boolean userPhoneDupChk;

    public static DupChkDto of(SignupDto signupDto) {
        DupChkDto dupChkDto = new DupChkDto();
        dupChkDto.setUserId(signupDto.getSignupUserId());
        dupChkDto.setUserEmail(signupDto.getUserEmailId() + "@" + signupDto.getUserEmailDomain());
        dupChkDto.setUserPhone(signupDto.getUserPhone());
        dupChkDto.setDupChkType("signup");
        return dupChkDto;
    }

    public static DupChkDto of(UserDto userDto) {
        DupChkDto dupChkDto = new DupChkDto();
        dupChkDto.setUserId(userDto.getUserId());
        dupChkDto.setUserEmail(userDto.getUserEmailId() + "@" + userDto.getUserEmailDomain());
        dupChkDto.setUserPhone(userDto.getUserPhone());
        dupChkDto.setDupChkType("setUser");
        return dupChkDto;
    }
}
