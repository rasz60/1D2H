package com.raszsixt._d2h.common.mail.constant;

import org.springframework.stereotype.Component;

@Component
public class MailConstant {
    public static final String FIND_INFO_TEMPLATE = "/mail-template/findInfo.html";
    public static final String FIND_ID_SUBJECT = "[1d2h] 이메일 주소로 가입된 아이디 입니다.";
    public static final String FIND_ID_BODY_TEXT = "안녕하세요, 1d2h 입니다.<br/>입력하신 이메일 주소로 가입된 아이디 리스트입니다.<br/><br/>[1d2h] 홈페이지로 이동하시어 다시 로그인 시도해주시길 바랍니다.<br/><br/>감사합니다.";
    public static final String FIND_PW_SUBJECT = "[1d2h] 임시 비밀번호 발급해드립니다.";
    public static final String FIND_PW_BODY_TEXT = "안녕하세요, 1d2h 입니다.<br/>입력하신 아이디의 임시 비밀번호가 발급되었습니다.<br/><br/>[1d2h] 홈페이지로 이동하시어 다시 로그인 시도해주시고,<br/>임시 비밀번호는 반드시 변경하시어 사용해주시길 바랍니다.<br/><br/>감사합니다.";
    public static final String CONTACT_ME_TEMPLATE = "/mail-template/contactMe.html";
    public static final String CONTACT_ME_SUBJECT = "[1d2h] 문의 메일이 접수되었습니다.";

}
