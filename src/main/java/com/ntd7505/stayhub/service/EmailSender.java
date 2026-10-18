package com.ntd7505.stayhub.service;

public interface EmailSender {
  void sendPasswordResetEmail(String recipient, String resetLink);
}
