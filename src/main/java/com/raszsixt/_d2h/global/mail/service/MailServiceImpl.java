package com.raszsixt._d2h.global.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender sender;
    @Value("${server.base-url}")
    private String baseUrl;

    @Value("${server.port}")
    private String port;

    public MailServiceImpl(JavaMailSender sender) {
        this.sender = sender;
    }
    public String sendFindInfoEmail(String toAddress, String subject, String bodyText, String info) {
        String result = "";
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            ClassPathResource resource = new ClassPathResource("/mail-template/findInfo.html");
            String mailHtml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            mailHtml = mailHtml
                        .replace("${subject}", subject)
                        .replace("${body}", bodyText)
                        .replace("${info}", info)
                        .replace("${link_url}", baseUrl + ":" + port);
            result = sendEmail(toAddress, subject, mailHtml);
        } catch (Exception e) {
            e.printStackTrace();
            result = "E";
        }
        return result;
    }

    public String sendEmail(String toAddress, String subject, String bodyText) {
        String result = "";
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(bodyText, true);
            helper.setFrom("1d2h-Admin <1d2hadm@gmail.com>");
            sender.send(message);
            result = "S";
        } catch (Exception e) {
            e.printStackTrace();
            result = "E";
        }
        return result;
    }
}
