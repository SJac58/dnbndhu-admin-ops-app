package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AttendanceRepository {

    public void markAttendance(int studentId, String date, String status) {

        String sql = """
            INSERT INTO attendance
            (student_id, attendance_date, status)
            VALUES (?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);
            ps.setString(2, date);
            ps.setString(3, status); // P or A

            ps.executeUpdate();

            System.out.println("✔ Attendance marked");

        } catch (Exception e) {
            throw new RuntimeException("Failed to mark attendance", e);
        }
    }
}
