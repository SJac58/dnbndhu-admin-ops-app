package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentRepository {

    public int save(Student student) {

        String sql = """
            INSERT INTO students
            (full_name, phone, gender, batch_id)
            VALUES (?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getPhone());
            ps.setString(3, student.getGender());
            ps.setInt(4, student.getBatchId());

            ps.executeUpdate();

            // 🔑 Get generated student_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int studentId = rs.getInt(1);
                System.out.println("✔ Student saved with ID: " + studentId);
                return studentId;
            }

            throw new RuntimeException("Student saved but ID not generated");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }
}
