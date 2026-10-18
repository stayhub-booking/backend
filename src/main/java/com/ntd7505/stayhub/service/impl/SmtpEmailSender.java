package com.ntd7505.stayhub.service.impl;

import com.ntd7505.stayhub.config.AppMailProperties;
import com.ntd7505.stayhub.service.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

  private final JavaMailSender mailSender;
  private final AppMailProperties properties;

  @Override
  public void sendPasswordResetEmail(String recipient, String resetLink) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setFrom(properties.from());
    message.setTo(recipient);
    message.setSubject("Reset your StayHub password");
    message.setText(
        """
                        We received a request to reset your StayHub password.

                        Open the following link:
                        %s

                        This link expires in 15 minutes and can only be used once.

                        If you did not request this, you can ignore this email.
                        """
            .formatted(resetLink));

    mailSender.send(message);
  }
}
