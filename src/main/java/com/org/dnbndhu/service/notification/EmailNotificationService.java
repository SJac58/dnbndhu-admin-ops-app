package com.org.dnbndhu.service.notification;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class EmailNotificationService {

    /**
     * Phase 1 - Mock Email Sender
     * Logs to console + stores in DB
     */
    public void sendEmail(String to, String subject, String body) {

        // 1️⃣ Print to console (simulate email sending)
        System.out.println("========================================");
        System.out.println("📧 MOCK EMAIL SENT");
        System.out.println("To      : " + to);
        System.out.println("Subject : " + subject);
        System.out.println("Message : " + body);
        System.out.println("Sent At : " + LocalDateTime.now());
        System.out.println("========================================");

        // 2️⃣ Save notification record in DB
        saveNotificationRecord(to, body);
    }

    private void saveNotificationRecord(String email, String message) {

        String sql = """
            INSERT INTO notifications (student_id, message, sent_date, status)
            VALUES (
                (SELECT student_id FROM students WHERE email = ?),
                ?,
                ?,
                'SENT'
            )
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, email);
            ps.setString(2, message);
            ps.setString(3, LocalDateTime.now().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("⚠ Failed to log notification to DB");
            e.printStackTrace();
        }
    }
}
