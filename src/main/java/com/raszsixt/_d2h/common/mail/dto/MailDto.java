package com.raszsixt._d2h.common.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {


    private String toAddr;
    private String fromAddr;
    private String subject;
    private String contents;
    private String type;
    private String info;

    public MailDto(String toAddr, String type, String info) {
        this.toAddr = toAddr;
        this.type = type;
        this.info = info;
    }
}
