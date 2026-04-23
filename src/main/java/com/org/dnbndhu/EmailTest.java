package com.org.dnbndhu;

import com.org.dnbndhu.service.notification.EmailNotificationService;

public class EmailTest {
    public static void main(String[] args) {
        EmailNotificationService emailService = new EmailNotificationService();

        emailService.sendEmail(
                1,
                "sara.jacob@btech.christuniversity.in",
                "SMTP Test",
                "If you got this, it works!"
        );
    }
}
