package com.raszsixt._d2h.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {
    private String signupUserId;
    private String signupUserPwd;
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
    private boolean signupUserIdDupChk;
    private boolean userEmailDupChk;
    private boolean userPhoneDupChk;
}
