package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.model.Qualification;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class QualificationRepository {

    public void save(Qualification q) {

        String sql = """
            INSERT INTO qualifications
            (student_id, education_level, institution, board_university, year_of_passing)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, q.getStudentId());
            ps.setString(2, q.getEducationLevel());
            ps.setString(3, q.getInstitution());
            ps.setString(4, q.getBoardUniversity());
            ps.setObject(5, q.getYearOfPassing());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Failed to save qualification", e);
        }
    }
}
