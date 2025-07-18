package com.raszsixt._d2h.modules.contact.controller;

import com.raszsixt._d2h.common.mail.dto.MailDto;
import com.raszsixt._d2h.common.mail.service.MailService;
import com.raszsixt._d2h.modules.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/sendmail")
    public ResponseEntity<?> sendMail(@RequestBody MailDto mailDto) {
        String res = contactService.sendMail(mailDto);
        return ResponseEntity.ok(res);
    }
}
