package com.raszsixt._d2h.global.mail.service;

import java.util.Map;

public interface MailService {
    public String sendFindInfoEmail(String toAddress, String subject, String bodyText, String info);
    public String sendEmail(String toAddress, String subject, String bodyText);
}
