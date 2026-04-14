package com.trungquan.nongsan.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
