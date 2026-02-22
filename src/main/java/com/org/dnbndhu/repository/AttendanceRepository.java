package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AttendanceRepository {

    // ==========================================
    // MARK / UPDATE ATTENDANCE
    // ==========================================
    public void markAttendance(int studentId, String date, String status) {

        String sql = """
            INSERT INTO attendance (student_id, attendance_date, status)
            VALUES (?, ?, ?)
            ON CONFLICT(student_id, attendance_date)
            DO UPDATE SET status = excluded.status
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);
            ps.setString(2, date);
            ps.setString(3, status);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to mark attendance for student ID: " + studentId, e);
        }
    }

    // ==========================================
    // GET ATTENDANCE FOR A SPECIFIC DATE (Batch)
    // Used by AttendanceController to pre-fill UI
    // ==========================================
    public Map<Integer, String> getAttendanceByDate(String date) {

        String sql = """
            SELECT student_id, status
            FROM attendance
            WHERE attendance_date = ?
        """;

        Map<Integer, String> attendanceMap = new HashMap<>();

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                attendanceMap.put(
                        rs.getInt("student_id"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch attendance by date", e);
        }

        return attendanceMap;
    }

    // ==========================================
    // COUNT CONSECUTIVE ABSENCES
    // Used for notification trigger
    // ==========================================
    public int countConsecutiveAbsences(int studentId) {

        String sql = """
            SELECT status
            FROM attendance
            WHERE student_id = ?
            ORDER BY attendance_date DESC
        """;

        int count = 0;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                if ("A".equalsIgnoreCase(rs.getString("status"))) {
                    count++;
                } else {
                    break;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check consecutive absences", e);
        }

        return count;
    }

    // ==========================================
    // GET ATTENDANCE % FOR STUDENT
    // (Optional - useful for profile screen)
    // ==========================================
    public double calculateAttendancePercentage(int studentId) {

        String sql = """
            SELECT 
                CASE 
                    WHEN COUNT(*) = 0 THEN 0
                    ELSE (SUM(CASE WHEN status = 'P' THEN 1 ELSE 0 END) * 100.0) / COUNT(*)
                END AS attendance_percent
            FROM attendance
            WHERE student_id = ?
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("attendance_percent");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate attendance percentage", e);
        }

        return 0;
    }
}
