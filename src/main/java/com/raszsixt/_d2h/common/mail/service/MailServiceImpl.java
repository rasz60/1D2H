package com.raszsixt._d2h.common.mail.service;

import com.raszsixt._d2h.common.mail.constant.MailConstant;
import com.raszsixt._d2h.common.mail.dto.MailDto;
import com.raszsixt._d2h.common.mail.entity.Mail;
import com.raszsixt._d2h.common.mail.repository.MailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender sender;
    private final MailConstant mailConstant;
    private final MailRepository mailRepository;
    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.port}")
    private String port;

    @Value("${spring.mail.pubmail}")
    private String pubMail;

    public String sendEmail(MailDto mailDto) {
        String result = "";
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            mailDto = this.setSendMailInfo(mailDto);

            helper.setTo(mailDto.getToAddr());
            helper.setSubject(mailDto.getSubject());
            helper.setText(mailDto.getContents(), true);
            helper.setFrom(mailDto.getFromAddr());
            sender.send(message);

            Mail mail = Mail.of(mailDto);
            mailRepository.save(mail);
            result = "S";
        } catch (Exception e) {
            e.printStackTrace();
            result = "E";
        }
        return result;
    }

    public MailDto setSendMailInfo(MailDto mailDto) throws IOException {

        String type = mailDto.getType();

        ClassPathResource resource = null;

        if ( "FIND_ID".equals(type) ) {
            resource = new ClassPathResource(mailConstant.FIND_INFO_TEMPLATE);
            mailDto.setSubject(mailConstant.FIND_ID_SUBJECT);
            mailDto.setContents(mailConstant.FIND_ID_BODY_TEXT);
        } else if ( "FIND_PW".equals(type) ) {
            resource = new ClassPathResource(mailConstant.FIND_INFO_TEMPLATE);
            mailDto.setSubject(mailConstant.FIND_PW_SUBJECT);
            mailDto.setContents(mailConstant.FIND_PW_BODY_TEXT);
        } else {
            resource = new ClassPathResource(mailConstant.CONTACT_ME_TEMPLATE);
            mailDto.setSubject(mailConstant.CONTACT_ME_SUBJECT);
        }

        String mailHtml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        mailHtml = mailHtml
                .replace("${subject}", mailDto.getSubject())
                .replace("${body}", mailDto.getContents())
                .replace("${fromAddr}", mailDto.getFromAddr());
        mailDto.setContents(mailHtml);

        String toAddr = mailDto.getToAddr();
        String fromAddr = mailDto.getFromAddr();

        if ( "".equals(toAddr) || toAddr == null ) {
            mailDto.setToAddr(pubMail);
        }

        if ( "".equals(fromAddr) || fromAddr == null ) {
            mailDto.setFromAddr(pubMail);
        }

        return mailDto;
    }
}
