package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.model.FamilyDetails;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FamilyDetailsRepository {

    public void save(FamilyDetails f) {

        String sql = """
            INSERT INTO family_details
            (student_id, member_name, relationship, income, phone)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, f.getStudentId());
            ps.setString(2, f.getMemberName());
            ps.setString(3, f.getRelationship());
            ps.setObject(4, f.getIncome());
            ps.setString(5, f.getPhone());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save family detail for student ID: " + f.getStudentId(), e);
        }
    }
}
