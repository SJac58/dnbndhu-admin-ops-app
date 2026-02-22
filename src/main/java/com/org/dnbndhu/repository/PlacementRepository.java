package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

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
}
