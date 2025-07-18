package com.raszsixt._d2h.common.mail.entity;

import com.raszsixt._d2h.common.mail.dto.MailDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "1D2H_MAIL_LOG")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Data
public class Mail {
    @Id
    @SequenceGenerator(
            name = "mail_seq_generator",
            sequenceName = "1d2h_mail_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "mail_seq_generator"
    )
    private Long mailNo;
    @Column(columnDefinition = "VARCHAR(500)")
    private String toAddr;
    @Column(columnDefinition = "VARCHAR(500)")
    private String fromAddr;
    @Column(columnDefinition = "VARCHAR(1000)")
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String contents;
    @Column(columnDefinition = "VARCHAR(100)")
    private String type;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendDate;

    public static Mail of (MailDto mailDto) {
        Mail mail = new Mail();

        mail.setToAddr(mailDto.getToAddr());
        mail.setFromAddr(mailDto.getFromAddr());
        mail.setSubject(mailDto.getSubject());
        mail.setContents(mailDto.getContents());
        mail.setType(mailDto.getType());

        return mail;
    }

}
