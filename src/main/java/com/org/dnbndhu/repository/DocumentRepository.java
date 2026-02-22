package com.org.dnbndhu.repository;

import com.org.dnbndhu.infrastructure.db.SQLiteConnectionManager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DocumentRepository {

    // ================= SAVE DOCUMENT =================
    public void saveDocument(
            int studentId,
            int documentTypeId,
            String filePath,
            double qualityScore,
            String qualityStatus
    ) {

        String sql = """
            INSERT INTO student_documents
            (student_id, document_type_id, file_name, file_path, quality_score, quality_status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            // Extract file name from full path
            File file = new File(filePath);
            String fileName = file.getName();

            ps.setInt(1, studentId);
            ps.setInt(2, documentTypeId);
            ps.setString(3, fileName);        // ✅ REQUIRED (NOT NULL column)
            ps.setString(4, filePath);
            ps.setDouble(5, qualityScore);
            ps.setString(6, qualityStatus);

            ps.executeUpdate();

            System.out.println("✔ Document saved: " + fileName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save document", e);
        }
    }

    // ================= FETCH DOCUMENTS FOR PROFILE =================
    public Map<Integer, String> findByStudentId(int studentId) {

        String sql = """
            SELECT document_type_id, file_path
            FROM student_documents
            WHERE student_id = ?
        """;

        Map<Integer, String> map = new HashMap<>();

        try (
                Connection conn = SQLiteConnectionManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                map.put(
                        rs.getInt("document_type_id"),
                        rs.getString("file_path")
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch documents", e);
        }

        return map;
    }
}