package com.org.dnbndhu.repository;

import com.org.dnbndhu.domain.model.Student;
import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentRepository {

    public void save(Student student) {

        String sql = """
            INSERT INTO students
            (full_name, phone, gender, batch_id)
            VALUES (?, ?, ?, ?)                      
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, student.getFullName());
            ps.setString(2, student.getPhone());
            ps.setString(3, student.getGender());
            ps.setInt(4, student.getBatchId());

            ps.executeUpdate();

            System.out.println("✔ Student saved with enrollment timestamp");

        } catch (Exception e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }
}
