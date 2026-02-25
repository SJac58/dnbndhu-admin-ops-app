package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlacementRepository {

    // =========================
    // SAVE PLACEMENT POST
    // =========================
    public void savePlacement(int companyId,
                              String jobRole,
                              String description) {

        String sql = """
            INSERT INTO placements (company_id, job_role, description, posted_date)
            VALUES (?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, companyId);
            ps.setString(2, jobRole);
            ps.setString(3, description);
            ps.setString(4, LocalDate.now().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save placement", e);
        }
    }
    public List<String> fetchRecentPlacementLogs(int limit) {

        String sql = """
        SELECT n.sent_date, s.full_name, n.message
        FROM notifications n
        JOIN students s ON n.student_id = s.student_id
        WHERE n.message LIKE 'Dear Candidate%'
        ORDER BY n.sent_date DESC
        LIMIT ?
    """;

        List<String> logs = new ArrayList<>();

        try (Connection conn = SQLiteConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String log = String.format(
                        "[%s] Sent to %s",
                        rs.getString("sent_date"),
                        rs.getString("full_name")
                );

                logs.add(log);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch placement logs", e);
        }

        return logs;
    }
}
