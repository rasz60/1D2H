package com.raszsixt._d2h.modules.contact.service.impl;

import com.raszsixt._d2h.common.mail.dto.MailDto;
import com.raszsixt._d2h.common.mail.service.MailService;
import com.raszsixt._d2h.modules.contact.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final MailService mailService;

    @Override
    public String sendMail(MailDto mailDto) {
        String res = "";

        mailDto.setType("CONTACT_ME");
        mailDto.setContents(mailDto.getContents().replaceAll("\\n", "<br/>"));
        res = mailService.sendEmail(mailDto);

        if ( "S".equals(res) ) {
            res = "메일 발송에 성공했습니다. 입력하신 메일 주소로 빠른 시일 내에 회신드리겠습니다. ;)";
        } else {
            res = "일시적 오류로 메일 발송에 실패하였습니다. 다시 시도해주시길 바랍니다.";
        }

        return res;
    }

}
