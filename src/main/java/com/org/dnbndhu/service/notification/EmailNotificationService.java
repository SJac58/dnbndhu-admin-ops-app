package com.org.dnbndhu.service.notification;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Properties;

public class EmailNotificationService {

    private final Properties mailProperties = new Properties();

    public EmailNotificationService() {
        loadMailConfig();
    }

    private void loadMailConfig() {
        try (InputStream input =
                     getClass().getClassLoader().getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("application.properties not found");
            }

            mailProperties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }

    public void sendEmail(int studentId, String to, String subject, String body) {

        try {

            String host = mailProperties.getProperty("mail.host");
            String port = mailProperties.getProperty("mail.port");
            String username = mailProperties.getProperty("mail.username");
            String password = mailProperties.getProperty("mail.password");
            String auth = mailProperties.getProperty("mail.auth");
            String starttls = mailProperties.getProperty("mail.starttls");

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.starttls.enable", starttls);

            Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("✅ Email successfully sent to " + to);

            saveNotificationRecord(studentId, body);

        } catch (Exception e) {
            System.out.println("❌ Failed to send email");
            e.printStackTrace();
        }
    }

    private void saveNotificationRecord(int studentId, String message) {

        String sql = """
            INSERT INTO notifications (student_id, message, sent_date, status)
            VALUES (?, ?, ?, 'SENT')
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);
            ps.setString(2, message);
            ps.setString(3, LocalDateTime.now().toString());

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("⚠ Failed to log notification to DB");
            e.printStackTrace();
        }
    }
}