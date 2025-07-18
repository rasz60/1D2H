package com.raszsixt._d2h.modules.contact.service;

import com.raszsixt._d2h.common.mail.dto.MailDto;

public interface ContactService {

    public String sendMail(MailDto mailDto);
}
