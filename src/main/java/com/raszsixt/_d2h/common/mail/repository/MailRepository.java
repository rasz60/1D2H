package com.raszsixt._d2h.common.mail.repository;

import com.raszsixt._d2h.common.mail.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}
