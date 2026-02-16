package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AttendanceRepository {
    public void markAttendance(int studentId, String date, String status) {

        String insertSql = """
        INSERT INTO attendance (student_id, attendance_date, status)
        VALUES (?, ?, ?)
        ON CONFLICT(student_id, attendance_date)
        DO UPDATE SET status = excluded.status
    """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(insertSql)
        ) {
            ps.setInt(1, studentId);
            ps.setString(2, date);
            ps.setString(3, status);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to mark attendance", e);
        }
    }


    /**
     * Count consecutive ABSENT days (latest first)
     */
    public int countConsecutiveAbsences(int studentId) {

        String sql = """
            SELECT status
            FROM attendance
            WHERE student_id = ?
            ORDER BY attendance_date DESC
            LIMIT 3
        """;

        int count = 0;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if ("A".equals(rs.getString("status"))) {
                    count++;
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to check absences", e);
        }

        return count;
    }
}
