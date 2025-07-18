package com.raszsixt._d2h.common.mail.service;

import com.raszsixt._d2h.common.mail.dto.MailDto;

public interface MailService {
    public String sendEmail(MailDto mailDto);
}
